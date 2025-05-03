package com.nagornov.CorporateMessenger.application.applicationService;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.nagornov.CorporateMessenger.application.dto.common.MinioFileDto;
import com.nagornov.CorporateMessenger.application.dto.message.*;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatInterface;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import com.nagornov.CorporateMessenger.domain.broker.producer.UnreadMessageProducer;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioMessageFileDomainService;
import com.nagornov.CorporateMessenger.domain.service.em.RedisMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.businessService.cassandra.CassandraChatBusinessService;
import com.nagornov.CorporateMessenger.domain.service.message.MessageFileService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.message.ReadMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MessageApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final CassandraChatBusinessService cassandraChatBusinessService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final ReadMessageService readMessageService;
    private final MessageFileService messageFileService;
    private final MinioMessageFileDomainService minioMessageFileDomainService;
    private final UnreadMessageProducer unreadMessageProducer;
    private final RedisMessageDomainService redisMessageDomainService;


    @Transactional
    public MessageResponse createMessage(CreateMessageRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(authInfo.getUserIdAsUUID());

        ChatInterface chatInterface = cassandraChatBusinessService.getAvailableById(request.getChatIdAsUUID());
        cassandraChatBusinessService.validateUserOwnership(chatInterface, postgresUser);

        Message message = new Message(
                Uuids.timeBased(),
                chatInterface.getId(),
                postgresUser.getId(),
                postgresUser.getUsername(),
                request.getContent(),
                false,
                false,
                false,
                Instant.now()
        );

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            message.markAsHasFiles();

            for (MultipartFile file : request.getFiles()) {

                String filePath = minioMessageFileDomainService.upload(file);

                MessageFile messageFile = new MessageFile(
                        Uuids.timeBased(),
                        message.getId(),
                        file.getOriginalFilename(),
                        filePath,
                        file.getContentType(),
                        Instant.now()
                );
                messageFileService.save(messageFile);
            }
        }
        cassandraMessageDomainService.save(message);

        chatInterface.updateLastMessageId(message.getId());
        cassandraChatBusinessService.update(chatInterface);

        unreadMessageProducer
                .sendToIncrementUnreadMessageCountForOther(postgresUser, chatInterface);

        redisMessageDomainService.leftSave(chatInterface.getId(), message, 1, TimeUnit.DAYS);

        return new MessageResponse(
                message,
                message.getIsRead() ? readMessageService.getAllByMessageId(message.getId()) : null,
                message.getHasFiles() ? messageFileService.getAllByMessageId(message.getId()) : null
        );
    }


    @Transactional(readOnly = true)
    public List<MessageResponse> getAllMessages(String chatId, int page, int size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(authInfo.getUserIdAsUUID());

        UUID uuidChatId = UUID.fromString(chatId);

        List<Message> messages = redisMessageDomainService.findAll(uuidChatId, page, size);

        if (messages.isEmpty()) {
            ChatInterface chatInterface = cassandraChatBusinessService.getAvailableById(uuidChatId);
            cassandraChatBusinessService.validateUserOwnership(chatInterface, postgresUser);

            messages = cassandraMessageDomainService.getAllByChatId(chatInterface.getId(), page, size);
            redisMessageDomainService.rightSaveAll(uuidChatId, messages, 1, TimeUnit.DAYS);
        }

        List<MessageResponse> listMessageResponses = new ArrayList<>();
        for (Message message : messages) {
            listMessageResponses.add(
                    new MessageResponse(
                            message,
                            message.getIsRead() ? readMessageService.getAllByMessageId(message.getId()) : null,
                            message.getHasFiles() ? messageFileService.getAllByMessageId(message.getId()) : null
                    )
            );
        }

        return listMessageResponses;
    }


    @Transactional
    public MessageResponse updateMessageContent(UpdateMessageContentRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Message message = cassandraMessageDomainService.getById(request.getMessageIdAsUUID());
        message.validateUserIdOwnership(authInfo.getUserIdAsUUID());
        message.validateContentBeforeUpdate(request.getContent());

        message.updateContent(request.getContent());
        message.markAsChanged();
        cassandraMessageDomainService.save(message);
        redisMessageDomainService.update(request.getChatIdAsUUID(), message);

        return new MessageResponse(
            message,
            message.getIsRead() ? readMessageService.getAllByMessageId(message.getId()) : null,
            message.getHasFiles() ? messageFileService.getAllByMessageId(message.getId()) : null
        );
    }


    @Transactional
    public MessageResponse deleteMessage(DeleteMessageRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(authInfo.getUserIdAsUUID());

        ChatInterface chatInterface = cassandraChatBusinessService.getAvailableById(request.getChatIdAsUUID());
        cassandraChatBusinessService.validateUserOwnership(chatInterface, postgresUser);

        Message message = cassandraMessageDomainService.getById(request.getMessageIdAsUUID());
        message.validateUserIdOwnership(postgresUser.getId());

        if (message.getHasFiles()) {
            List<MessageFile> messageFiles = messageFileService.getAllByMessageId(message.getId());
            for (MessageFile messageFile : messageFiles) {
                messageFileService.delete(messageFile);
                minioMessageFileDomainService.delete(messageFile.getFilePath());
            }
        }
        cassandraMessageDomainService.delete(message);
        redisMessageDomainService.delete(chatInterface.getId(), message);

        Optional<Message> existingPreviousMessage = cassandraMessageDomainService.findLastByChatId(chatInterface.getId());
        chatInterface.updateLastMessageId(
                existingPreviousMessage.map(Message::getId).orElse(null)
        );
        cassandraChatBusinessService.update(chatInterface);

        readMessageService.getAllByMessageId(message.getId())
                .forEach(readMessageService::delete);

        if (!message.getIsRead()) {
            unreadMessageProducer
                    .sendToDecrementUnreadMessageCountForOther(postgresUser, chatInterface);
        }

        return new MessageResponse(message, null, null);
    }


    @Transactional
    public MessageResponse readMessage(ReadMessageRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(authInfo.getUserIdAsUUID());

        ChatInterface chatInterface = cassandraChatBusinessService.getAvailableById(request.getChatIdAsUUID());
        cassandraChatBusinessService.validateUserOwnership(chatInterface, postgresUser);

        Message message = cassandraMessageDomainService.getById(request.getMessageIdAsUUID());
        chatInterface.validateMessageChatIdOwnership(message.getChatId());

        if (message.getSenderId().equals(postgresUser.getId())) {
            return null;
        }
        if (readMessageService.checkExistsByMessageIdAndUserId(message.getId(), postgresUser.getId())) {
            return null;
        }

        message.markAsRead();
        cassandraMessageDomainService.save(message);
        redisMessageDomainService.update(chatInterface.getId(), message);

        ReadMessage readMessage = new ReadMessage(
                UUID.randomUUID(),
                postgresUser.getId(),
                chatInterface.getId(),
                message.getId()
        );
        readMessageService.save(readMessage);

        unreadMessageProducer
                .sendToDecrementUnreadMessageCountForUser(postgresUser, chatInterface);

        return new MessageResponse(
                message,
                message.getIsRead() ? readMessageService.getAllByMessageId(message.getId()) : null,
                message.getHasFiles() ? messageFileService.getAllByMessageId(message.getId()) : null
        );
    }


    public MinioFileDto getMessageFile(String chatId, String messageId, String fileId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(authInfo.getUserIdAsUUID());

        ChatInterface chatInterface = cassandraChatBusinessService.getAvailableById(UUID.fromString(chatId));
        cassandraChatBusinessService.validateUserOwnership(chatInterface, postgresUser);

        Message message = cassandraMessageDomainService.getById(UUID.fromString(messageId));
        Optional<MessageFile> messageFile = messageFileService.findByIdAndMessageId(
                UUID.fromString(fileId), message.getId()
        );

        return new MinioFileDto(
                new InputStreamResource(
                        minioMessageFileDomainService.download(messageFile.get().getFilePath())
                ),
                minioMessageFileDomainService.statObject(messageFile.get().getFilePath())
        );
    }

}
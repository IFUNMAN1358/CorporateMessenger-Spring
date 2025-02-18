package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.MinioFileDto;
import com.nagornov.CorporateMessenger.application.dto.message.*;
import com.nagornov.CorporateMessenger.domain.factory.MessageFactory;
import com.nagornov.CorporateMessenger.domain.factory.MessageFileFactory;
import com.nagornov.CorporateMessenger.domain.factory.ReadMessageFactory;
import com.nagornov.CorporateMessenger.domain.model.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaUnreadMessageProducerDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioMessageFileDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import com.nagornov.CorporateMessenger.domain.service.businessService.cassandra.CassandraChatBusinessService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraMessageFileDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraReadMessageDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageApplicationService {

    private final JwtDomainService jwtDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final CassandraChatBusinessService cassandraChatBusinessService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final CassandraReadMessageDomainService cassandraReadMessageDomainService;
    private final CassandraMessageFileDomainService cassandraMessageFileDomainService;
    private final MinioMessageFileDomainService minioMessageFileDomainService;
    private final KafkaUnreadMessageProducerDomainService kafkaUnreadMessageProducerDomainService;


    @Transactional
    public MessageResponse create(@NotNull CreateMessageRequest request) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        final User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        final Chat chat = cassandraChatBusinessService.getAvailableById(request.getChatIdAsUUID());
        cassandraChatBusinessService.validateUserOwnership(chat, postgresUser);

        final Message message = MessageFactory.createWithRandomId();
        message.updateChatId(chat.getId());
        message.updateSenderId(postgresUser.getId());
        message.updateSenderFirstName(postgresUser.getFirstName());
        message.updateSenderUsername(postgresUser.getUsername());
        message.updateContent(request.getContent());

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            message.markAsHasFiles();

            for (MultipartFile file : request.getFiles()) {
                final MessageFile messageFile = MessageFileFactory.createFromMultipartFile(file);
                messageFile.updateMessageId(message.getId());

                cassandraMessageFileDomainService.save(messageFile);
                minioMessageFileDomainService.upload(messageFile, file);
            }
        }
        cassandraMessageDomainService.save(message);

        chat.updateLastMessageId(message.getId());
        cassandraChatBusinessService.update(chat);

        kafkaUnreadMessageProducerDomainService
                .sendToIncrementUnreadMessageCountForOther(postgresUser, chat);

        return new MessageResponse(
                message,
                message.getIsRead() ? cassandraReadMessageDomainService.getAllByMessageId(message.getId()) : null,
                message.getHasFiles() ? cassandraMessageFileDomainService.getAllByMessageId(message.getId()) : null
        );
    }


    @Transactional(readOnly = true)
    public List<MessageResponse> getAll(@NotNull String chatId, @NotNull int page, @NotNull int size) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        final User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        final Chat chat = cassandraChatBusinessService.getAvailableById(UUID.fromString(chatId));
        cassandraChatBusinessService.validateUserOwnership(chat, postgresUser);

        final List<MessageResponse> listMessageResponses = new ArrayList<>();
        final List<Message> listOfMessages = cassandraMessageDomainService.getAllByChatId(chat.getId(), page, size);

        for (Message message : listOfMessages) {
            listMessageResponses.add(
                    new MessageResponse(
                            message,
                            message.getIsRead() ? cassandraReadMessageDomainService.getAllByMessageId(message.getId()) : null,
                            message.getHasFiles() ? cassandraMessageFileDomainService.getAllByMessageId(message.getId()) : null
                    )
            );
        }
        return listMessageResponses;
    }


    @Transactional
    public MessageResponse updateContent(@NotNull UpdateMessageContentRequest request) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final Message message = cassandraMessageDomainService.getById(request.getMessageIdAsUUID());
        message.validateUserIdOwnership(authInfo.getUserIdAsUUID());
        message.validateContentBeforeUpdate(request.getContent());

        message.updateContent(request.getContent());
        message.markAsChanged();
        cassandraMessageDomainService.update(message);

        return new MessageResponse(
            message,
            message.getIsRead() ? cassandraReadMessageDomainService.getAllByMessageId(message.getId()) : null,
            message.getHasFiles() ? cassandraMessageFileDomainService.getAllByMessageId(message.getId()) : null
        );
    }


    @Transactional
    public MessageResponse delete(@NotNull DeleteMessageRequest request) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        final User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        final Chat chat = cassandraChatBusinessService.getAvailableById(request.getChatIdAsUUID());
        cassandraChatBusinessService.validateUserOwnership(chat, postgresUser);

        final Message message = cassandraMessageDomainService.getById(request.getMessageIdAsUUID());
        message.validateUserIdOwnership(postgresUser.getId());

        if (message.getHasFiles()) {
            final List<MessageFile> messageFiles = cassandraMessageFileDomainService.getAllByMessageId(message.getId());
            for (MessageFile messageFile : messageFiles) {
                cassandraMessageFileDomainService.delete(messageFile);
                minioMessageFileDomainService.delete(messageFile.getFilePath());
            }
        }
        cassandraMessageDomainService.delete(message);

        Optional<Message> existingPreviousMessage = cassandraMessageDomainService.findLastByChatId(chat.getId());
        chat.updateLastMessageId(
                existingPreviousMessage.map(Message::getId).orElse(null)
        );
        cassandraChatBusinessService.update(chat);

        cassandraReadMessageDomainService.getAllByMessageId(message.getId())
                .forEach(cassandraReadMessageDomainService::delete);

        if (!message.getIsRead()) {
            kafkaUnreadMessageProducerDomainService
                    .sendToDecrementUnreadMessageCountForOther(postgresUser, chat);
        }

        return new MessageResponse(message, null, null);
    }


    @Transactional
    public MessageResponse read(@NotNull ReadMessageRequest request) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        final User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        final Chat chat = cassandraChatBusinessService.getAvailableById(request.getChatIdAsUUID());
        cassandraChatBusinessService.validateUserOwnership(chat, postgresUser);

        final Message message = cassandraMessageDomainService.getById(request.getMessageIdAsUUID());
        chat.validateMessageChatIdOwnership(message.getChatId());

        if (message.getSenderId().equals(postgresUser.getId())) {
            return null;
        }
        if (cassandraReadMessageDomainService.checkExistsByMessageIdAndUserId(message.getId(), postgresUser.getId())) {
            return null;
        }

        message.markAsRead();
        cassandraMessageDomainService.update(message);

        final ReadMessage readMessage = ReadMessageFactory.createWithRandomId();
        readMessage.updateUserId(postgresUser.getId());
        readMessage.updateChatId(chat.getId());
        readMessage.updateMessageId(message.getId());
        cassandraReadMessageDomainService.save(readMessage);

        kafkaUnreadMessageProducerDomainService
                .sendToDecrementUnreadMessageCountForUser(postgresUser, chat);

        return new MessageResponse(
                message,
                message.getIsRead() ? cassandraReadMessageDomainService.getAllByMessageId(message.getId()) : null,
                message.getHasFiles() ? cassandraMessageFileDomainService.getAllByMessageId(message.getId()) : null
        );
    }


    public MinioFileDto getFile(@NotNull String chatId, @NotNull String messageId, @NotNull String fileId) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        final User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        final Chat chat = cassandraChatBusinessService.getAvailableById(UUID.fromString(chatId));
        cassandraChatBusinessService.validateUserOwnership(chat, postgresUser);

        final Message message = cassandraMessageDomainService.getById(UUID.fromString(messageId));
        final Optional<MessageFile> messageFile = cassandraMessageFileDomainService.findByIdAndMessageId(
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
package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.message.*;
import com.nagornov.CorporateMessenger.domain.dto.MinioFileDTO;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatMemberService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatService;
import com.nagornov.CorporateMessenger.domain.service.message.MessageService;
import com.nagornov.CorporateMessenger.domain.service.user.UserBlacklistService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import com.nagornov.CorporateMessenger.domain.broker.producer.UnreadMessageProducer;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.message.MessageFileService;
import com.nagornov.CorporateMessenger.domain.service.message.ReadMessageService;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MessageApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final ChatService chatService;
    private final ChatMemberService chatMemberService;
    private final MessageService messageService;
    private final ReadMessageService readMessageService;
    private final MessageFileService messageFileService;
    private final UnreadMessageProducer unreadMessageProducer;
    private final UserBlacklistService userBlacklistService;


    @Transactional
    public MessageResponse createMessage(@NotNull Long chatId, @NonNull MessageRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        Chat chat = chatService.getById(chatId);

        if (chat.isPrivateChat()) {
            List<ChatMember> privateChatMembers = chatMemberService.findAllByChatId(chatId);
            userBlacklistService.ensureAnyMatchBetweenUserIds(
                    authUser.getId(),
                    privateChatMembers.stream()
                            .filter(cm -> !cm.getUserId().equals(authUser.getId()))
                            .toList().getFirst().getUserId()
            );
        }
        chatMemberService.ensureExistsByChatIdAndUserId(chat.getId(), authUser.getId());

        Message message;
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {

            message = messageService.create(chat.getId(), authUser.getId(), authUser.getUsername(), request.getContent(), true);

            for (MultipartFile file : request.getFiles()) {
                messageFileService.upload(message.getId(), file);
            }

        } else {
            message = messageService.create(chat.getId(), authUser.getId(), authUser.getUsername(), request.getContent(), false);
        }

        unreadMessageProducer.sendToIncrementUnreadMessageCountForOther(chat, authUser);

        messageService.leftSaveToRedis(chat.getId(), message, 1, TimeUnit.DAYS);

        return new MessageResponse(
                message,
                List.of(),
                message.getHasFiles() ? messageFileService.findAllByMessageId(message.getId()) : List.of()
        );
    }


    @Transactional(readOnly = true)
    public List<MessageResponse> getAllMessages(@NotNull Long chatId, int page, int size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        List<Message> messages = messageService.findAllFromRedis(chatId, page, size);

        if (messages.isEmpty()) {

            Chat chat = chatService.getById(chatId);
            chatMemberService.ensureExistsByChatIdAndUserId(chat.getId(), authUser.getId());

            messages = messageService.findAllByChatId(chat.getId(), page, size);
            messageService.rightSaveAllToRedis(chat.getId(), messages, 1, TimeUnit.DAYS);
        }

        List<MessageResponse> listMessageResponses = new ArrayList<>();
        for (Message message : messages) {
            listMessageResponses.add(
                    new MessageResponse(
                            message,
                            message.getIsRead() ? readMessageService.findAllByMessageId(message.getId()) : List.of(),
                            message.getHasFiles() ? messageFileService.findAllByMessageId(message.getId()) : List.of()
                    )
            );
        }
        return listMessageResponses;
    }


    @Transactional(readOnly = true)
    public MinioFileDTO downloadMessageFile(@NonNull Long chatId, @NonNull UUID messageId, @NonNull UUID fileId, @NonNull String size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chatMemberService.ensureExistsByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());

        MessageFile messageFile = messageFileService.getByIdAndMessageId(fileId, messageId);

        return messageFileService.download(messageFile, size);
    }


    @Transactional
    public MessageResponse readMessage(@NonNull Long chatId, @NonNull UUID messageId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        Chat chat = chatService.getById(chatId);
        chatMemberService.ensureExistsByChatIdAndUserId(chat.getId(), authUser.getId());

        Message message = messageService.getByChatIdAndId(chatId, messageId);

        if (message.getSenderId().equals(authUser.getId())) {
            return null;
        }
        if (readMessageService.existsByMessageIdAndUserId(message.getId(), authUser.getId())) {
            return null;
        }

        message.markAsRead();
        messageService.update(message);
        messageService.updateToRedis(chatId, message);

        readMessageService.create(authUser.getId(), messageId);

        unreadMessageProducer.sendToDecrementUnreadMessageCountForUser(chat, authUser);

        return new MessageResponse(
                message,
                message.getIsRead() ? readMessageService.findAllByMessageId(message.getId()) : List.of(),
                message.getHasFiles() ? messageFileService.findAllByMessageId(message.getId()) : List.of()
        );
    }


    @Transactional
    public MessageResponse updateMessageContent(@NonNull Long chatId, @NonNull UUID messageId, @NonNull MessageContentRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Message message = messageService.getByChatIdAndId(chatId, messageId);
        message.ensureUserIdIsSenderId(authInfo.getUserIdAsUUID());

        if (
                message.getHasFiles()
                        &&
                (message.getContent() == null || message.getContent().trim().isEmpty())
                        &&
                (request.getNewContent() == null || request.getNewContent().trim().isEmpty())
        ) {
            throw new RuntimeException("Message cannot be without files and without content");
        }

        message.updateContent(request.getNewContent());
        message.markAsChanged();
        messageService.update(message);
        messageService.updateToRedis(chatId, message);

        return new MessageResponse(
            message,
            message.getIsRead() ? readMessageService.findAllByMessageId(message.getId()) : List.of(),
            message.getHasFiles() ? messageFileService.findAllByMessageId(message.getId()) : List.of()
        );
    }


    @Transactional
    public MessageResponse deleteMessage(@NonNull Long chatId, @NonNull UUID messageId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        Chat chat = chatService.getById(chatId);
        chatMemberService.ensureExistsByChatIdAndUserId(chat.getId(), authUser.getId());

        Message message = messageService.getByChatIdAndId(chatId, messageId);
        message.ensureUserIdIsSenderId(authUser.getId());

        if (message.getHasFiles()) {
            messageFileService.deleteAllByMessageId(message.getId());
        }
        messageService.delete(message);
        messageService.deleteFromRedis(chat.getId(), message);

        readMessageService.deleteAllByMessageId(message.getId());

        if (!message.getIsRead()) {
            unreadMessageProducer.sendToDecrementUnreadMessageCountForOther(chat, authUser);
        }

        return new MessageResponse(message, List.of(), List.of());
    }
}
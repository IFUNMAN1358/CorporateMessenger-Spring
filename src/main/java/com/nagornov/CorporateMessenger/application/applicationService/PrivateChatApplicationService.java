package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.chat.ChatDTO;
import com.nagornov.CorporateMessenger.application.dto.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.ChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserIdRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.enums.ChatType;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrivateChatApplicationService {

    private final JwtDomainService jwtDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserPhotoDomainService jpaUserPhotoDomainService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final CassandraChatDomainService cassandraChatDomainService;
    private final CassandraChatMemberDomainService cassandraChatMemberDomainService;
    private final CassandraChatPhotoDomainService cassandraChatPhotoDomainService;
    private final CassandraPrivateChatDomainService cassandraPrivateChatDomainService;
    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;

    public ChatDTO createPrivateChat(@NonNull UserIdRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        User user = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());
        User partner = jpaUserDomainService.getById(request.getUserIdAsUUID());

        String userPairHash = PrivateChat.generateUserPairHash(user.getId(), partner.getId());
        Optional<PrivateChat> optPrivateChat = cassandraPrivateChatDomainService.findByUserPairHash(userPairHash);

        Chat chat;
        if (optPrivateChat.isPresent()) {

            chat = cassandraChatDomainService.getById(optPrivateChat.get().getChatId());

        } else {

            chat = new Chat(
                    Chat.generateId(),
                    ChatType.PRIVATE.getType(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Instant.now(),
                    Instant.now()
            );
            cassandraChatDomainService.save(chat);

            PrivateChat privateChat = new PrivateChat(userPairHash, chat.getId());
            cassandraPrivateChatDomainService.save(privateChat);
        }

        return new ChatDTO();
    }

    public ChatSummaryResponse getOrCreatePrivateChat(UserIdRequest request) {

        UserPhoto secondUserPhoto =
                jpaUserPhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

        PrivateChat privateChat;
        if (existingPrivateChat.isEmpty()) {

            privateChat = new PrivateChat(
                    UUID.randomUUID(),
                    postgresFirstUser.getId(),
                    postgresSecondUser.getId(),
                    null,
                    Instant.now(),
                    true
            );
            cassandraPrivateChatDomainService.save(privateChat);

            cassandraUnreadMessageDomainService.save(
                    new UnreadMessage(privateChat.getId(), postgresFirstUser.getId(), 0)
            );
            cassandraUnreadMessageDomainService.save(
                    new UnreadMessage(privateChat.getId(), postgresSecondUser.getId(), 0)
            );
        } else {
            privateChat = existingPrivateChat.get();
        }

        return new ChatSummaryResponse(
                new UserResponseWithMainPhoto(postgresSecondUser, secondUserPhoto),
                privateChat,
                privateChat.getLastMessageId() != null ? cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null,
                cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresFirstUser.getId())
        );
    }


    public ChatSummaryResponse getPrivateChat(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        PrivateChat privateChat = cassandraPrivateChatDomainService.getAvailableById(
                UUID.fromString(chatId)
        );
        privateChat.validateUserIdOwnership(postgresUser.getId());

        User postgresSecondUser = jpaUserDomainService.getById(
                privateChat.getCompanionUserId(postgresUser.getId())
        );

        UserPhoto secondUserPhoto =
                jpaUserPhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

        return new ChatSummaryResponse(
                new UserResponseWithMainPhoto(postgresSecondUser, secondUserPhoto),
                privateChat,
                privateChat.getLastMessageId() != null ? cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null,
                cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresUser.getId())
        );
    }


    public List<ChatSummaryResponse> getAllPrivateChats() {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        List<PrivateChat> chatsAsFirstUser =
                cassandraPrivateChatDomainService.getAllAvailableByFirstUserId(postgresUser.getId());
        List<PrivateChat> chatsAsSecondUser =
                cassandraPrivateChatDomainService.getAllAvailableBySecondUserId(postgresUser.getId());

        List<PrivateChat> allPrivateChats = new ArrayList<>();
        allPrivateChats.addAll(chatsAsFirstUser);
        allPrivateChats.addAll(chatsAsSecondUser);

        return allPrivateChats.stream().map((PrivateChat privateChat) -> {

            User postgresSecondUser = jpaUserDomainService.getById(
                    privateChat.getCompanionUserId(postgresUser.getId())
            );

            UserPhoto secondUserPhoto =
                    jpaUserPhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

            Message existingMessage = privateChat.getLastMessageId() != null ?
                    cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null;

            UnreadMessage unreadMessageAsFirstUser =
                    cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresUser.getId());

            return new ChatSummaryResponse(
                    new UserResponseWithMainPhoto(postgresSecondUser, secondUserPhoto),
                    privateChat,
                    existingMessage,
                    unreadMessageAsFirstUser
            );
        }).toList();
    }


    public HttpResponse markPrivateChatAsUnavailable(ChatIdRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(authInfo.getUserIdAsUUID());

        PrivateChat privateChat = cassandraPrivateChatDomainService.getAvailableById(
                request.getChatIdAsUUID()
        );
        privateChat.validateUserIdOwnership(postgresUser.getId());
        privateChat.markAsUnavailable();
        cassandraPrivateChatDomainService.save(privateChat);

        return new HttpResponse("Private chat is unavailable", 200);
    }

}

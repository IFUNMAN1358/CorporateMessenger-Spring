package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.SecondUserIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.PrivateChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.factory.PrivateChatFactory;
import com.nagornov.CorporateMessenger.domain.model.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraPrivateChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraUnreadMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserProfilePhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrivateChatApplicationService {

    private final JwtDomainService jwtDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserProfilePhotoDomainService jpaUserProfilePhotoDomainService;
    private final CassandraPrivateChatDomainService cassandraPrivateChatDomainService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;

    public PrivateChatSummaryResponse getOrCreate(@NotNull SecondUserIdRequest request) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresFirstUser =
                jpaUserDomainService.getById(UUID.fromString(authInfo.getUserId()));
        final User postgresSecondUser =
                jpaUserDomainService.getById(UUID.fromString(request.getSecondUserId()));

        final Optional<PrivateChat> existingPrivateChat = cassandraPrivateChatDomainService
                .findAvailableByFirstUserIdAndSecondUserId(postgresFirstUser.getId(), postgresSecondUser.getId())
                .or(() -> cassandraPrivateChatDomainService
                        .findAvailableByFirstUserIdAndSecondUserId(postgresSecondUser.getId(), postgresFirstUser.getId())
                );

        final UserProfilePhoto secondUserProfilePhoto =
                jpaUserProfilePhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

        PrivateChat privateChat;
        if (existingPrivateChat.isEmpty()) {

            privateChat = PrivateChatFactory.createWithRandomId();
            privateChat.updateFirstUserId(postgresFirstUser.getId());
            privateChat.updateSecondUserId(postgresSecondUser.getId());
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

        return new PrivateChatSummaryResponse(
                new UserResponseWithMainPhoto(postgresSecondUser, secondUserProfilePhoto),
                privateChat,
                privateChat.getLastMessageId() != null ? cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null,
                cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresFirstUser.getId())
        );
    }


    public PrivateChatSummaryResponse get(@NotNull String chatId) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        final PrivateChat privateChat = cassandraPrivateChatDomainService.getAvailableById(
                UUID.fromString(chatId)
        );
        privateChat.validateUserIdOwnership(postgresUser.getId());

        final User postgresSecondUser = jpaUserDomainService.getById(
                privateChat.getCompanionUserId(postgresUser.getId())
        );

        final UserProfilePhoto secondUserProfilePhoto =
                jpaUserProfilePhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

        return new PrivateChatSummaryResponse(
                new UserResponseWithMainPhoto(postgresSecondUser, secondUserProfilePhoto),
                privateChat,
                privateChat.getLastMessageId() != null ? cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null,
                cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresUser.getId())
        );
    }


    public List<PrivateChatSummaryResponse> getAll() {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        final List<PrivateChat> chatsAsFirstUser =
                cassandraPrivateChatDomainService.getAllAvailableByFirstUserId(postgresUser.getId());
        final List<PrivateChat> chatsAsSecondUser =
                cassandraPrivateChatDomainService.getAllAvailableBySecondUserId(postgresUser.getId());

        final List<PrivateChat> allPrivateChats = new ArrayList<>();
        allPrivateChats.addAll(chatsAsFirstUser);
        allPrivateChats.addAll(chatsAsSecondUser);

        return allPrivateChats.stream().map((PrivateChat privateChat) -> {

            final User postgresSecondUser = jpaUserDomainService.getById(
                    privateChat.getCompanionUserId(postgresUser.getId())
            );

            final UserProfilePhoto secondUserProfilePhoto =
                    jpaUserProfilePhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

            final Message existingMessage = privateChat.getLastMessageId() != null ?
                    cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null;

            final UnreadMessage unreadMessageAsFirstUser =
                cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresUser.getId());

            return new PrivateChatSummaryResponse(
                    new UserResponseWithMainPhoto(postgresSecondUser, secondUserProfilePhoto),
                    privateChat,
                    existingMessage,
                    unreadMessageAsFirstUser
            );
        }).toList();
    }


    public HttpResponse markAsUnavailable(@NotNull ChatIdRequest request) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        final PrivateChat privateChat = cassandraPrivateChatDomainService.getAvailableById(
                UUID.fromString(request.getChatId())
        );
        privateChat.validateUserIdOwnership(postgresUser.getId());
        privateChat.markAsUnavailable();
        cassandraPrivateChatDomainService.update(privateChat);

        return new HttpResponse("Private chat is unavailable", 200);
    }

}

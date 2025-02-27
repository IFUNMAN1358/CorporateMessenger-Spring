package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.SecondUserIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.PrivateChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.factory.PrivateChatFactory;
import com.nagornov.CorporateMessenger.domain.logger.ApplicationServiceLogger;
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
    private final ApplicationServiceLogger applicationServiceLogger;

    public PrivateChatSummaryResponse getOrCreatePrivateChat(@NotNull SecondUserIdRequest request) {
        try {
            applicationServiceLogger.info("Get or create private chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresFirstUser =
                    jpaUserDomainService.getById(UUID.fromString(authInfo.getUserId()));

            User postgresSecondUser =
                    jpaUserDomainService.getById(UUID.fromString(request.getSecondUserId()));

            Optional<PrivateChat> existingPrivateChat = cassandraPrivateChatDomainService
                    .findAvailableByFirstUserIdAndSecondUserId(postgresFirstUser.getId(), postgresSecondUser.getId())
                    .or(() -> cassandraPrivateChatDomainService
                            .findAvailableByFirstUserIdAndSecondUserId(postgresSecondUser.getId(), postgresFirstUser.getId())
                    );

            UserProfilePhoto secondUserProfilePhoto =
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

            applicationServiceLogger.info("Get or create private chat finished");

            return new PrivateChatSummaryResponse(
                    new UserResponseWithMainPhoto(postgresSecondUser, secondUserProfilePhoto),
                    privateChat,
                    privateChat.getLastMessageId() != null ? cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null,
                    cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresFirstUser.getId())
            );

        } catch (Exception e) {
            applicationServiceLogger.error("Get or create private chat failed", e);
            throw e;
        }
    }


    public PrivateChatSummaryResponse getPrivateChat(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Get private chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    UUID.fromString(authInfo.getUserId())
            );

            PrivateChat privateChat = cassandraPrivateChatDomainService.getAvailableById(
                    UUID.fromString(chatId)
            );
            privateChat.validateUserIdOwnership(postgresUser.getId());

            User postgresSecondUser = jpaUserDomainService.getById(
                    privateChat.getCompanionUserId(postgresUser.getId())
            );

            UserProfilePhoto secondUserProfilePhoto =
                    jpaUserProfilePhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

            applicationServiceLogger.info("Get private chat finished");

            return new PrivateChatSummaryResponse(
                    new UserResponseWithMainPhoto(postgresSecondUser, secondUserProfilePhoto),
                    privateChat,
                    privateChat.getLastMessageId() != null ? cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null,
                    cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresUser.getId())
            );

        } catch (Exception e) {
            applicationServiceLogger.error("Get private chat failed", e);
            throw e;
        }
    }


    public List<PrivateChatSummaryResponse> getAllPrivateChats() {
        try {
            applicationServiceLogger.info("Get all private chats started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    UUID.fromString(authInfo.getUserId())
            );

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

                UserProfilePhoto secondUserProfilePhoto =
                        jpaUserProfilePhotoDomainService.findMainByUserId(postgresSecondUser.getId()).orElse(null);

                Message existingMessage = privateChat.getLastMessageId() != null ?
                        cassandraMessageDomainService.getById(privateChat.getLastMessageId()) : null;

                UnreadMessage unreadMessageAsFirstUser =
                        cassandraUnreadMessageDomainService.getByChatIdAndUserId(privateChat.getId(), postgresUser.getId());

                applicationServiceLogger.info("Get all private chats finished");

                return new PrivateChatSummaryResponse(
                        new UserResponseWithMainPhoto(postgresSecondUser, secondUserProfilePhoto),
                        privateChat,
                        existingMessage,
                        unreadMessageAsFirstUser
                );
            }).toList();

        } catch (Exception e) {
            applicationServiceLogger.error("Get all private chats failed", e);
            throw e;
        }
    }


    public HttpResponse markPrivateChatAsUnavailable(@NotNull ChatIdRequest request) {
        try {
            applicationServiceLogger.info("Mark private chat as unavailable started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    UUID.fromString(authInfo.getUserId())
            );

            PrivateChat privateChat = cassandraPrivateChatDomainService.getAvailableById(
                    UUID.fromString(request.getChatId())
            );
            privateChat.validateUserIdOwnership(postgresUser.getId());
            privateChat.markAsUnavailable();
            cassandraPrivateChatDomainService.update(privateChat);

            applicationServiceLogger.info("Mark private chat as unavailable finished");

            return new HttpResponse("Private chat is unavailable", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Mark private chat as unavailable failed", e);
            throw e;
        }
    }

}

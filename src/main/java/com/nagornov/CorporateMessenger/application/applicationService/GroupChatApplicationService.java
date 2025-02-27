package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.GroupChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.chat.UpdateGroupChatMetadataRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.common.UserIdRequest;
import com.nagornov.CorporateMessenger.domain.factory.GroupChatFactory;
import com.nagornov.CorporateMessenger.domain.factory.GroupChatMemberFactory;
import com.nagornov.CorporateMessenger.domain.logger.ApplicationServiceLogger;
import com.nagornov.CorporateMessenger.domain.model.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraUnreadMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioGroupChatPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupChatApplicationService {

    private final JwtDomainService jwtDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final CassandraGroupChatDomainService cassandraGroupChatDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;
    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final MinioGroupChatPhotoDomainService minioGroupChatPhotoDomainService;
    private final ApplicationServiceLogger applicationServiceLogger;


    @Transactional
    public HttpResponse createGroupChat(@NotNull CreateGroupChatRequest request) {
        try {
            applicationServiceLogger.info("Create group chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    authInfo.getUserIdAsUUID()
            );

            GroupChat groupChat = GroupChatFactory.createWithRandomId();
            if (request.getDescription() != null) {
                groupChat.updateDescription(request.getDescription());
            }

            if (request.getFile() != null) {
                String filePath = minioGroupChatPhotoDomainService.upload(request.getFile());
                groupChat.updateFilePath(filePath);
            }

            if (request.getIsPublic()) {
                groupChat.markAsPublic();
            } else {
                groupChat.markAsPrivate();
            }
            groupChat.updateName(request.getName());
            groupChat.updateOwnerId(postgresUser.getId());
            cassandraGroupChatDomainService.save(groupChat);

            GroupChatMember member = GroupChatMemberFactory.createWithRandomId();
            member.updateChatId(groupChat.getId());
            member.updateUserId(postgresUser.getId());
            member.updateUserFirstName(postgresUser.getFirstName());
            cassandraGroupChatMemberDomainService.save(member);

            cassandraUnreadMessageDomainService.save(
                    new UnreadMessage(groupChat.getId(), postgresUser.getId(), 0)
            );

            applicationServiceLogger.info("Create group chat finished");

            return new HttpResponse("Group chat has been created", 201);

        } catch (Exception e) {
            applicationServiceLogger.error("Create group chat failed", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public List<GroupChatSummaryResponse> getAllGroupChats() {
        try {
            applicationServiceLogger.info("Get all group chats started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    authInfo.getUserIdAsUUID()
            );

            List<GroupChatSummaryResponse> listOfResponse = new ArrayList<>();

            for (GroupChatMember member : cassandraGroupChatMemberDomainService.getAllByUserId(postgresUser.getId())) {

                GroupChat groupChat = cassandraGroupChatDomainService.getById(member.getChatId());
                Message lastMessage = null;
                User lastMessageSender = null;

                if (groupChat.getLastMessageId() != null) {
                    lastMessage = cassandraMessageDomainService.getById(groupChat.getLastMessageId());
                    lastMessageSender = jpaUserDomainService.getById(lastMessage.getSenderId());
                }

                UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                        groupChat.getId(), postgresUser.getId()
                );

                GroupChatSummaryResponse summaryResponse = new GroupChatSummaryResponse(
                        groupChat, lastMessage, lastMessageSender, unreadMessage
                );

                listOfResponse.add(summaryResponse);
            }

            applicationServiceLogger.info("Get all group chats finished");

            return listOfResponse;

        } catch (Exception e) {
            applicationServiceLogger.error("Get all group chats failed", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public GroupChat getGroupChat(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Get group chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            UUID chatIdAsUUID = UUID.fromString(chatId);

            cassandraGroupChatMemberDomainService.validateUserOwnership(chatIdAsUUID, authInfo.getUserIdAsUUID());

            GroupChat groupChat = cassandraGroupChatDomainService.getById(chatIdAsUUID);

            applicationServiceLogger.info("Get group chat finished");

            return groupChat;

        } catch (Exception e) {
            applicationServiceLogger.error("Get group chat failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse changeGroupChatMetadata(@NotNull String chatId, @NotNull UpdateGroupChatMetadataRequest request) {
        try {
            applicationServiceLogger.info("Change group chat metadata started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

            groupChat.updateName(request.getNewName());
            groupChat.updateDescription(request.getNewDescription());
            cassandraGroupChatDomainService.update(groupChat);

            applicationServiceLogger.info("Change group chat metadata finished");

            return new HttpResponse("Group chat metadata has been changed", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Change group chat metadata failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse changeGroupChatPublicStatus(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Change group chat public status started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

            if (groupChat.getIsPublic()) {
                groupChat.markAsPrivate();
            } else {
                groupChat.markAsPublic();
            }
            cassandraGroupChatDomainService.update(groupChat);

            applicationServiceLogger.info("Change group chat public status finished");

            return new HttpResponse("Group chat status has been changed", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Change group chat public status failed", e);
            throw e;
        }
    }


    public HttpResponse changeGroupChatOwner(@NotNull String chatId, @NotNull UserIdRequest request) {
        try {
            applicationServiceLogger.info("Change group chat owner started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

            GroupChatMember futureOwner = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
                    groupChat.getId(), UUID.fromString(request.getUserId())
            );

            groupChat.updateOwnerId(futureOwner.getUserId());
            cassandraGroupChatDomainService.update(groupChat);

            applicationServiceLogger.info("Change group chat owner finished");

            return new HttpResponse("New owner of group chat has been changed", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Change group chat owner failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse deleteGroupChat(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Delete group chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

            List<GroupChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
            for (GroupChatMember member : members) {
                cassandraGroupChatMemberDomainService.delete(member);
            }

            groupChat.updateOwnerId(null);
            cassandraGroupChatDomainService.update(groupChat);

            applicationServiceLogger.info("Delete group chat finished");

            return new HttpResponse("Group chat has been deleted", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Delete group chat failed", e);
            throw e;
        }
    }

}
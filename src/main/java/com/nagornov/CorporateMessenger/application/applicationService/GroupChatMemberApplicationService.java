package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.common.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponse;
import com.nagornov.CorporateMessenger.domain.factory.GroupChatMemberFactory;
import com.nagornov.CorporateMessenger.domain.logger.ApplicationServiceLogger;
import com.nagornov.CorporateMessenger.domain.model.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraPrivateChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraUnreadMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupChatMemberApplicationService {

    private final JwtDomainService jwtDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;
    private final CassandraPrivateChatDomainService cassandraPrivateChatDomainService;
    private final CassandraGroupChatDomainService cassandraGroupChatDomainService;
    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final ApplicationServiceLogger applicationServiceLogger;


    @Transactional(readOnly = true)
    public List<GroupChatMember> getGroupChatMembers(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Get group chat members started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            UUID userId = authInfo.getUserIdAsUUID();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            cassandraGroupChatMemberDomainService.validateUserOwnership(groupChat.getId(), userId);

            applicationServiceLogger.info("Get group chat members finished");

            return cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());

        } catch (Exception e) {
            applicationServiceLogger.error("Get group chat members failed", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public List<UserResponse> getAvailableUsersToAdding(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Get available users to add started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            UUID userId = authInfo.getUserIdAsUUID();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            groupChat.validateOwnerIdOwnership(userId);

            List<UUID> companionIds =
                    cassandraPrivateChatDomainService.getAllAvailableByAllUserId(userId)
                            .stream().map(privateChat ->
                                    privateChat.getFirstUserId().equals(userId) ?
                                            privateChat.getSecondUserId() : privateChat.getFirstUserId()
                            ).toList();

            List<UUID> memberIds =
                    cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId())
                            .stream().map(
                                    GroupChatMember::getUserId
                            ).toList();

            applicationServiceLogger.info("Get available users to add finished");

            return companionIds
                    .stream().filter(companionId -> !memberIds.contains(companionId))
                    .map(companionId -> {

                        User userToAdd = jpaUserDomainService.getById(companionId);
                        return new UserResponse(userToAdd);

                    }).toList();

        } catch (Exception e) {
            applicationServiceLogger.error("Get available users to add failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse addMembersToGroupChat(@NotNull String chatId, @NotNull UserIdsRequest request) {
        try {
            applicationServiceLogger.info("Add members to group chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

            for (String requestUserId : request.getUserIds()) {

                User user = jpaUserDomainService.getById(
                        UUID.fromString(requestUserId)
                );

                GroupChatMember chatMember = GroupChatMemberFactory.createWithRandomId();
                chatMember.updateChatId(groupChat.getId());
                chatMember.updateUserId(user.getId());
                chatMember.updateUserFirstName(user.getFirstName());
                cassandraGroupChatMemberDomainService.save(chatMember);

                UnreadMessage unreadMessage = new UnreadMessage(groupChat.getId(), user.getId(), 0);
                cassandraUnreadMessageDomainService.save(unreadMessage);
            }

            applicationServiceLogger.info("Add members to group chat finished");

            return new HttpResponse("User/s added to group chat", 201);

        } catch (Exception e) {
            applicationServiceLogger.error("Add members to group chat failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse deleteMembersFromGroupChat(@NotNull String chatId, @NotNull UserIdsRequest request) {
        try {
            applicationServiceLogger.info("Delete members from group chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );
            groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

            for (String requestUserId : request.getUserIds()) {

                GroupChatMember member = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
                        groupChat.getId(), UUID.fromString(requestUserId)
                );
                cassandraGroupChatMemberDomainService.delete(member);

                UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                        groupChat.getId(), member.getUserId()
                );
                cassandraUnreadMessageDomainService.delete(unreadMessage);
            }

            applicationServiceLogger.info("Delete members from group chat finished");

            return new HttpResponse("Members of group chat has been deleted", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Delete members from group chat failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse leaveFromGroupChat(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Leave from group chat started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            GroupChat groupChat = cassandraGroupChatDomainService.getById(
                    UUID.fromString(chatId)
            );

            GroupChatMember member = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
                    groupChat.getId(), authInfo.getUserIdAsUUID()
            );

            if (member.getUserId().equals(groupChat.getOwnerId())) {
                throw new RuntimeException("Owner of the group chat can't leave it");
            }

            cassandraGroupChatMemberDomainService.delete(member);

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    groupChat.getId(), member.getUserId()
            );
            cassandraUnreadMessageDomainService.delete(unreadMessage);

            applicationServiceLogger.info("Leave from group chat finished");

            return new HttpResponse("You left the group chat", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Leave from group chat failed", e);
            throw e;
        }
    }

}

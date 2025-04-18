package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponse;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraPrivateChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraUnreadMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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


    @Transactional(readOnly = true)
    public List<ChatMember> getGroupChatMembers(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        cassandraGroupChatMemberDomainService.validateUserOwnership(groupChat.getId(), authInfo.getUserIdAsUUID());

        return cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
    }


    @Transactional(readOnly = true)
    public List<UserResponse> getAvailableUsersToAdding(String chatId) {

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
                                ChatMember::getUserId
                        ).toList();

        return companionIds
                .stream().filter(companionId -> !memberIds.contains(companionId))
                .map(companionId -> {

                    User userToAdd = jpaUserDomainService.getById(companionId);
                    return new UserResponse(userToAdd);

                }).toList();
    }


    @Transactional
    public HttpResponse addMembersToGroupChat(String chatId, UserIdsRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        for (String requestUserId : request.getUserIds()) {

            User user = jpaUserDomainService.getById(
                    UUID.fromString(requestUserId)
            );

            ChatMember chatMember = new ChatMember(
                UUID.randomUUID(),
                groupChat.getId(),
                user.getId(),
                Instant.now()
            );
            cassandraGroupChatMemberDomainService.save(chatMember);

            UnreadMessage unreadMessage = new UnreadMessage(groupChat.getId(), user.getId(), 0);
            cassandraUnreadMessageDomainService.save(unreadMessage);
        }

        return new HttpResponse("User/s added to group chat", 201);
    }


    @Transactional
    public HttpResponse deleteMembersFromGroupChat(String chatId, UserIdsRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        for (String requestUserId : request.getUserIds()) {

            ChatMember member = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
                    groupChat.getId(), UUID.fromString(requestUserId)
            );
            cassandraGroupChatMemberDomainService.delete(member);

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    groupChat.getId(), member.getUserId()
            );
            cassandraUnreadMessageDomainService.delete(unreadMessage);
        }

        return new HttpResponse("Members of group chat has been deleted", 200);
    }


    @Transactional
    public HttpResponse leaveFromGroupChat(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );

        ChatMember member = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
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

        return new HttpResponse("You left the group chat", 200);
    }

}

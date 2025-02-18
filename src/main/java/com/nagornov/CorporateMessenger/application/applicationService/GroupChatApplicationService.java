package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.GroupChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.chat.UpdateGroupChatMetadataRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.common.UserIdRequest;
import com.nagornov.CorporateMessenger.domain.factory.GroupChatFactory;
import com.nagornov.CorporateMessenger.domain.factory.GroupChatMemberFactory;
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


    @Transactional
    public HttpResponse createGroupChat(@NotNull CreateGroupChatRequest request) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(
                authInfo.getUserIdAsUUID()
        );

        GroupChat groupChat = GroupChatFactory.createWithRandomId();
        if (request.getDescription() != null) {
            groupChat.updateDescription(request.getDescription());
        };
        if (request.getFile() != null) {
            String filePath = minioGroupChatPhotoDomainService.upload(request.getFile());
            groupChat.updateFilePath(filePath);
        };
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

        return new HttpResponse("Group chat has been created", 201);
    }


    @Transactional(readOnly = true)
    public List<GroupChatSummaryResponse> getAllGroupChats() {
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

        return listOfResponse;
    }


    @Transactional(readOnly = true)
    public GroupChat getGroupChat(@NotNull String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        UUID chatIdAsUUID = UUID.fromString(chatId);

        cassandraGroupChatMemberDomainService.validateUserOwnership(chatIdAsUUID, authInfo.getUserIdAsUUID());

        return cassandraGroupChatDomainService.getById(chatIdAsUUID);
    }


    @Transactional
    public HttpResponse changeGroupChatMetadata(@NotNull String chatId, @NotNull UpdateGroupChatMetadataRequest request) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        groupChat.updateName(request.getNewName());
        groupChat.updateDescription(request.getNewDescription());
        cassandraGroupChatDomainService.update(groupChat);

        return new HttpResponse("Group chat metadata has been changed", 200);
    }


    @Transactional
    public HttpResponse changeGroupChatPublicStatus(@NotNull String chatId) {
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

        return new HttpResponse("Group chat status has been changed", 200);
    }


    public HttpResponse changeGroupChatOwner(@NotNull String chatId, @NotNull UserIdRequest request) {

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

        return new HttpResponse("New owner of group chat has been changed", 200);
    }


    @Transactional
    public HttpResponse deleteGroupChat(@NotNull String chatId) {
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

        return new HttpResponse("Group chat has been deleted", 200);
    }

}
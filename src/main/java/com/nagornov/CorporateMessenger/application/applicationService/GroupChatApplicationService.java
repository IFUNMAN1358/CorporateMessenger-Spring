package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.GroupChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.chat.UpdateGroupChatMetadataRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserIdRequest;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioGroupChatPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
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
    private final CassandraGroupChatPhotoDomainService cassandraGroupChatPhotoDomainService;
    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final MinioGroupChatPhotoDomainService minioGroupChatPhotoDomainService;


    @Transactional
    public HttpResponse createGroupChat(CreateGroupChatRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(
                authInfo.getUserIdAsUUID()
        );

        GroupChat groupChat = new GroupChat(
                UUID.randomUUID(),
                request.getName(),
                null,
                postgresUser.getId(),
                null,
                null,
                false,
                Instant.now(),
                Instant.now()
        );
        if (request.getDescription() != null) {
            groupChat.updateDescription(request.getDescription());
        }
        if (request.getFile() != null) {

            MultipartFile file = request.getFile();
            String filePath = minioGroupChatPhotoDomainService.upload(file);

            ChatPhoto chatPhoto = new ChatPhoto(
                    UUID.randomUUID(),
                    groupChat.getId(),
                    file.getOriginalFilename(),
                    filePath,
                    file.getContentType(),
                    Instant.now()
            );
            cassandraGroupChatPhotoDomainService.save(chatPhoto);
        }
        if (request.getIsPublic()) {
            groupChat.markAsPublic();
        } else {
            groupChat.markAsPrivate();
        }
        cassandraGroupChatDomainService.save(groupChat);

        ChatMember member = new ChatMember(
                UUID.randomUUID(),
                groupChat.getId(),
                postgresUser.getId(),
                Instant.now()
        );
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

        for (ChatMember member : cassandraGroupChatMemberDomainService.getAllByUserId(postgresUser.getId())) {

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
    public GroupChat getGroupChat(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        UUID chatIdAsUUID = UUID.fromString(chatId);

        cassandraGroupChatMemberDomainService.validateUserOwnership(chatIdAsUUID, authInfo.getUserIdAsUUID());

        return cassandraGroupChatDomainService.getById(chatIdAsUUID);
    }


    @Transactional
    public HttpResponse changeGroupChatMetadata(String chatId, UpdateGroupChatMetadataRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        groupChat.updateName(request.getNewName());
        groupChat.updateDescription(request.getNewDescription());
        cassandraGroupChatDomainService.save(groupChat);

        return new HttpResponse("Group chat metadata has been changed", 200);
    }


    @Transactional
    public HttpResponse changeGroupChatPublicStatus(String chatId) {

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
        cassandraGroupChatDomainService.save(groupChat);

        return new HttpResponse("Group chat status has been changed", 200);
    }


    public HttpResponse changeGroupChatOwner(String chatId, UserIdRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        ChatMember futureOwner = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
                groupChat.getId(), request.getUserIdAsUUID()
        );

        groupChat.updateOwnerId(futureOwner.getUserId());
        cassandraGroupChatDomainService.save(groupChat);

        return new HttpResponse("New owner of group chat has been changed", 200);
    }


    @Transactional
    public HttpResponse deleteGroupChat(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        List<ChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
        for (ChatMember member : members) {
            cassandraGroupChatMemberDomainService.delete(member);
        }

        groupChat.updateOwnerId(null);
        cassandraGroupChatDomainService.save(groupChat);

        return new HttpResponse("Group chat has been deleted", 200);
    }

}
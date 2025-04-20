package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.chat.UpdateGroupChatMetadataRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserIdRequest;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.*;
import com.nagornov.CorporateMessenger.domain.service.UserService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioChatPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupChatApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final CassandraChatDomainService cassandraChatDomainService;
    private final CassandraChatMemberDomainService cassandraChatMemberDomainService;
    private final CassandraChatPhotoDomainService cassandraGroupChatPhotoDomainService;
    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final MinioChatPhotoDomainService minioChatPhotoDomainService;

    @Transactional
    public HttpResponse changeGroupChatMetadata(String chatId, UpdateGroupChatMetadataRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

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

        JwtAuthentication authInfo = jwtService.getAuthInfo();

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

        JwtAuthentication authInfo = jwtService.getAuthInfo();

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

        JwtAuthentication authInfo = jwtService.getAuthInfo();

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
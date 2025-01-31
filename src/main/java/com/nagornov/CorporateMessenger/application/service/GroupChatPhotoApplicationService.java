package com.nagornov.CorporateMessenger.application.service;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.InformationalResponse;
import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraUnreadMessageDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserProfilePhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioGroupChatPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupChatPhotoApplicationService {

    private final JwtDomainService jwtDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserProfilePhotoDomainService jpaUserProfilePhotoDomainService;
    private final CassandraGroupChatDomainService cassandraGroupChatDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;
    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;
    private final CassandraMessageDomainService cassandraMessageDomainService;
    private final MinioGroupChatPhotoDomainService minioGroupChatPhotoDomainService;


    @Transactional(readOnly = true)
    public Resource getPhoto(@NotNull String chatId) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        cassandraGroupChatMemberDomainService.validateUserOwnership(UUID.fromString(chatId), authInfo.getUserIdAsUUID());

        GroupChat groupChat = cassandraGroupChatDomainService.getById(UUID.fromString(chatId));

        return new InputStreamResource(
                minioGroupChatPhotoDomainService.download(groupChat.getFilePath())
        );
    }


    @Transactional
    public Resource uploadOrChangePhoto(@NotNull String chatId, @NotNull FileRequest request) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        if (groupChat.getFilePath() != null) {
            minioGroupChatPhotoDomainService.delete(groupChat.getFilePath());
        }

        String filePath = minioGroupChatPhotoDomainService.upload(request.getFile());
        groupChat.updateFilePath(filePath);
        cassandraGroupChatDomainService.update(groupChat);

        return new InputStreamResource(
                minioGroupChatPhotoDomainService.download(groupChat.getFilePath())
        );
    }


    @Transactional
    public InformationalResponse deletePhoto(@NotNull String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        if (groupChat.getFilePath() != null) {
            minioGroupChatPhotoDomainService.delete(groupChat.getFilePath());
        }

        groupChat.updateFilePath(null);
        cassandraGroupChatDomainService.update(groupChat);

        return new InformationalResponse("Photo has been successfully deleted");
    }

}

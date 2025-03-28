package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioGroupChatPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
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
    private final CassandraGroupChatDomainService cassandraGroupChatDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;
    private final MinioGroupChatPhotoDomainService minioGroupChatPhotoDomainService;


    @Transactional(readOnly = true)
    public Resource getGroupChatPhoto(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        cassandraGroupChatMemberDomainService.validateUserOwnership(UUID.fromString(chatId), authInfo.getUserIdAsUUID());

        GroupChat groupChat = cassandraGroupChatDomainService.getById(UUID.fromString(chatId));

        return new InputStreamResource(
                minioGroupChatPhotoDomainService.download(groupChat.getFilePath())
        );
    }


    @Transactional
    public Resource uploadOrChangeGroupChatPhoto(String chatId, FileRequest request) {

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
        cassandraGroupChatDomainService.save(groupChat);

        return new InputStreamResource(
                minioGroupChatPhotoDomainService.download(groupChat.getFilePath())
        );
    }


    @Transactional
    public HttpResponse deleteGroupChatPhoto(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        if (groupChat.getFilePath() != null) {
            minioGroupChatPhotoDomainService.delete(groupChat.getFilePath());
        }

        groupChat.updateFilePath(null);
        cassandraGroupChatDomainService.save(groupChat);

        return new HttpResponse("Photo has been successfully deleted", 200);
    }

}

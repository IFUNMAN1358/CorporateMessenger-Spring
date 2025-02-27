package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.logger.ApplicationServiceLogger;
import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
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
    private final CassandraGroupChatDomainService cassandraGroupChatDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;
    private final MinioGroupChatPhotoDomainService minioGroupChatPhotoDomainService;
    private final ApplicationServiceLogger applicationServiceLogger;


    @Transactional(readOnly = true)
    public Resource getGroupChatPhoto(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Get group chat photo started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            cassandraGroupChatMemberDomainService.validateUserOwnership(UUID.fromString(chatId), authInfo.getUserIdAsUUID());

            GroupChat groupChat = cassandraGroupChatDomainService.getById(UUID.fromString(chatId));

            applicationServiceLogger.info("Get group chat photo finished");

            return new InputStreamResource(
                    minioGroupChatPhotoDomainService.download(groupChat.getFilePath())
            );
        } catch (Exception e) {
            applicationServiceLogger.error("Get group chat photo failed", e);
            throw e;
        }
    }


    @Transactional
    public Resource uploadOrChangeGroupChatPhoto(@NotNull String chatId, @NotNull FileRequest request) {
        try {
            applicationServiceLogger.info("Upload or change group chat photo started");

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

            applicationServiceLogger.info("Upload or change group chat photo finished");

            return new InputStreamResource(
                    minioGroupChatPhotoDomainService.download(groupChat.getFilePath())
            );
        } catch (Exception e) {
            applicationServiceLogger.error("Upload or change group chat photo failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse deleteGroupChatPhoto(@NotNull String chatId) {
        try {
            applicationServiceLogger.info("Delete group chat photo started");

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

            applicationServiceLogger.info("Delete group chat photo finished");

            return new HttpResponse("Photo has been successfully deleted", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Delete group chat photo failed", e);
            throw e;
        }
    }

}

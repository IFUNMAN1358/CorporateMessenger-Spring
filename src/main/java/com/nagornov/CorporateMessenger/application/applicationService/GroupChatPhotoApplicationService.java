package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatPhoto;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioGroupChatPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupChatPhotoApplicationService {

    private final JwtDomainService jwtDomainService;
    private final CassandraGroupChatDomainService cassandraGroupChatDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;
    private final CassandraGroupChatPhotoDomainService cassandraGroupChatPhotoDomainService;
    private final MinioGroupChatPhotoDomainService minioGroupChatPhotoDomainService;


    @Transactional(readOnly = true)
    public Resource getGroupChatPhoto(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        cassandraGroupChatMemberDomainService.validateUserOwnership(UUID.fromString(chatId), authInfo.getUserIdAsUUID());

        GroupChat groupChat = cassandraGroupChatDomainService.getById(UUID.fromString(chatId));

        GroupChatPhoto groupChatPhoto = cassandraGroupChatPhotoDomainService.getFirstByChatId(groupChat.getId());

        return new InputStreamResource(
                minioGroupChatPhotoDomainService.download(groupChatPhoto.getFilePath())
        );
    }


    @Transactional
    public Resource uploadOrChangeGroupChatPhoto(String chatId, FileRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        Optional<GroupChatPhoto> groupChatPhoto = cassandraGroupChatPhotoDomainService.findFirstByChatId(groupChat.getId());

        if (groupChatPhoto.isPresent()) {
            cassandraGroupChatPhotoDomainService.delete(groupChatPhoto.get());
            minioGroupChatPhotoDomainService.delete(groupChatPhoto.get().getFilePath());
        }

        MultipartFile file = request.getFile();
        String filePath = minioGroupChatPhotoDomainService.upload(file);

        GroupChatPhoto newGroupChatPhoto = new GroupChatPhoto(
                UUID.randomUUID(),
                groupChat.getId(),
                file.getOriginalFilename(),
                filePath,
                file.getContentType(),
                Instant.now()
        );
        cassandraGroupChatPhotoDomainService.save(newGroupChatPhoto);

        groupChat.markAsHasPhotos();
        cassandraGroupChatDomainService.save(groupChat);

        return new InputStreamResource(
                minioGroupChatPhotoDomainService.download(newGroupChatPhoto.getFilePath())
        );
    }


    @Transactional
    public HttpResponse deleteGroupChatPhoto(String chatId) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        UUID chatIdUuid = UUID.fromString(chatId);
        GroupChat groupChat = cassandraGroupChatDomainService.getById(chatIdUuid);
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        GroupChatPhoto groupChatPhoto = cassandraGroupChatPhotoDomainService.getFirstByChatId(chatIdUuid);
        cassandraGroupChatPhotoDomainService.delete(groupChatPhoto);

        minioGroupChatPhotoDomainService.delete(groupChatPhoto.getFilePath());

        groupChat.markAsHasNotPhotos();
        cassandraGroupChatDomainService.save(groupChat);

        return new HttpResponse("Photo has been successfully deleted", 200);
    }

}

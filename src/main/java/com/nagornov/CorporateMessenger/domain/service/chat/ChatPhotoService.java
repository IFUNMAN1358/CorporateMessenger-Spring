package com.nagornov.CorporateMessenger.domain.service.chat;

import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import com.nagornov.CorporateMessenger.domain.enums.minio.MinioBucket;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.utils.ContentTypeUtils;
import com.nagornov.CorporateMessenger.domain.utils.InputStreamUtils;
import com.nagornov.CorporateMessenger.domain.utils.MinioUtils;
import com.nagornov.CorporateMessenger.domain.utils.ScalrUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatPhotoRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatPhotoService {

    private final CassandraChatPhotoRepository cassandraChatPhotoRepository;
    private final MinioRepository minioRepository;

    public ChatPhoto upload(@NonNull Long chatId, @NonNull MultipartFile file) {
        try {
            ContentTypeUtils.validateAsImageFromContentType(file.getContentType());

            BufferedImage originalImage = InputStreamUtils.inputStreamToBufferedImage(file);
            String originalFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
            InputStream originalIS = InputStreamUtils.bufferedImageToInputStream(originalImage, file.getContentType());
            minioRepository.upload(MinioBucket.CHAT_PHOTOS, originalFilePath, originalIS, "jpg");

            BufferedImage smallImage = ScalrUtils.resizeImage(originalImage, ImageSize.SIZE_128);
            String smallFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
            InputStream smallIS = InputStreamUtils.bufferedImageToInputStream(smallImage, file.getContentType());
            minioRepository.upload(MinioBucket.CHAT_PHOTOS, smallFilePath, smallIS, "jpg");

            ChatPhoto chatPhoto = new ChatPhoto(
                    UUID.randomUUID(),
                    chatId,
                    file.getOriginalFilename(),
                    smallFilePath,
                    originalFilePath,
                    "jpg",
                    true,
                    Instant.now()
            );
            return cassandraChatPhotoRepository.save(chatPhoto);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    public ChatPhoto update(@NonNull ChatPhoto chatPhoto) {
        return cassandraChatPhotoRepository.save(chatPhoto);
    }

    public List<ChatPhoto> updateAll(@NonNull List<ChatPhoto> chatPhotos) {
        return cassandraChatPhotoRepository.saveAll(chatPhotos);
    }

    public void delete(@NonNull ChatPhoto chatPhoto) {
        cassandraChatPhotoRepository.delete(chatPhoto);
    }

    public Resource download(@NonNull ChatPhoto chatPhoto, @NonNull String size) {
        try {
            InputStream inputStream;
            if (size.equals("big")) {
                inputStream = minioRepository.download(MinioBucket.CHAT_PHOTOS, chatPhoto.getBigFilePath());
            } else if (size.equals("small")) {
                inputStream = minioRepository.download(MinioBucket.CHAT_PHOTOS, chatPhoto.getSmallFilePath());
            } else {
                throw new ResourceBadRequestException("Invalid size param for downloading chat photo");
            }
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    public Resource downloadByFilePath(@NonNull String filePath) {
        return new InputStreamResource(
                minioRepository.download(MinioBucket.CHAT_PHOTOS, filePath)
        );
    }

    public List<ChatPhoto> findAllByChatId(@NonNull Long chatId) {
        List<ChatPhoto> chatPhotos = new LinkedList<>();
        for (ChatPhoto chatPhoto : cassandraChatPhotoRepository.findAllByChatId(chatId)) {
            if (chatPhoto.getIsMain()) {
                chatPhotos.addFirst(chatPhoto);
            } else {
                chatPhotos.add(chatPhoto);
            }
        }
        return chatPhotos;
    }

    public ChatPhoto getByIdAndChatId(@NonNull UUID id, @NonNull Long chatId) {
        return cassandraChatPhotoRepository.findByIdAndChatId(id, chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatPhoto[id=%s, chatId=%s] not found".formatted(id, chatId)));
    }

    public Optional<ChatPhoto> findMainByChatId(@NonNull Long chatId) {
        List<ChatPhoto> chatPhotos = findAllByChatId(chatId);
        for (ChatPhoto chatPhoto : chatPhotos) {
            if (chatPhoto.getIsMain()) {
                return Optional.of(chatPhoto);
            }
        }
        return Optional.empty();
    }

    public ChatPhoto getMainByChatId(@NonNull Long chatId) {
        List<ChatPhoto> chatPhotos = findAllByChatId(chatId);
        for (ChatPhoto chatPhoto : chatPhotos) {
            if (chatPhoto.getIsMain()) {
                return chatPhoto;
            }
        }
        throw new ResourceNotFoundException("ChatPhoto[chatId=%s, isMain=true] not found".formatted(chatId));
    }

}

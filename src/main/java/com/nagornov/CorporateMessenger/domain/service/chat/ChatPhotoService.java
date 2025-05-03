package com.nagornov.CorporateMessenger.domain.service.chat;

import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import com.nagornov.CorporateMessenger.domain.enums.minio.MinioBucket;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.utils.ContentTypeUtils;
import com.nagornov.CorporateMessenger.domain.utils.ScalrUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatPhotoRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatPhotoService {

    private final CassandraChatPhotoRepository cassandraChatPhotoRepository;
    private final MinioRepository minioRepository;

    public ChatPhoto upload(@NonNull Long chatId, @NonNull MultipartFile file) {
        try {
            ContentTypeUtils.validateAsImageFromContentType(file.getContentType());

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            String originalFilePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioRepository.upload(MinioBucket.CHAT_PHOTOS, originalFilePath, originalImage, "jpg");

            BufferedImage smallImage = ScalrUtils.resizeImage(originalImage, ImageSize.SIZE_128);
            String smallFilePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioRepository.upload(MinioBucket.CHAT_PHOTOS, smallFilePath, smallImage, "jpg");

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

    public void delete(@NonNull ChatPhoto chatPhoto) {
        cassandraChatPhotoRepository.delete(chatPhoto);
    }

    public Resource download(@NonNull String filePath) {
        return new InputStreamResource(
                minioRepository.download(MinioBucket.CHAT_PHOTOS, filePath)
        );
    }

    public List<ChatPhoto> findAllByChatId(@NonNull Long chatId) {
        return cassandraChatPhotoRepository.findAllByChatId(chatId);
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

}

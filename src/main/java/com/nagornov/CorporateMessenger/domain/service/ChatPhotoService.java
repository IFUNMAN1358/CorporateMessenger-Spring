package com.nagornov.CorporateMessenger.domain.service;

import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import com.nagornov.CorporateMessenger.domain.enums.MinioBucket;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.utils.ScalrUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatPhotoRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file format - file should be a image");
        }

        try {
            BufferedImage bigImage = ImageIO.read(file.getInputStream());
            if (bigImage == null) {
                throw new IllegalArgumentException("Invalid image format");
            }
            BufferedImage smallImage = ScalrUtils.resizeImage(bigImage, ImageSize.SIZE_128.getSize());

            String smallFilePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String bigFilePath = UUID.randomUUID() + "_" + file.getOriginalFilename();

            minioRepository.upload(
                MinioBucket.CHAT_PHOTOS,
                smallFilePath,
                smallImage,
                mimeType
            );
            minioRepository.upload(
                MinioBucket.CHAT_PHOTOS,
                bigFilePath,
                bigImage,
                mimeType
            );

            ChatPhoto chatPhoto = new ChatPhoto(
                    UUID.randomUUID(),
                    chatId,
                    file.getOriginalFilename(),
                    smallFilePath,
                    bigFilePath,
                    mimeType,
                    true,
                    Instant.now()
            );
            return cassandraChatPhotoRepository.save(chatPhoto);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void delete(@NonNull ChatPhoto chatPhoto) {
        minioRepository.delete(MinioBucket.CHAT_PHOTOS, chatPhoto.getSmallFilePath());
        minioRepository.delete(MinioBucket.CHAT_PHOTOS, chatPhoto.getBigFilePath());
        cassandraChatPhotoRepository.delete(chatPhoto);
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

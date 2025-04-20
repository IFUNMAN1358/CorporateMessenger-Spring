package com.nagornov.CorporateMessenger.domain.service.domainService.minio;

import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import com.nagornov.CorporateMessenger.domain.enums.MinioBucket;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.utils.ScalrUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioChatPhotoDomainService {

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
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image format");
            }

            BufferedImage smallImage = ScalrUtils.resizeImage(originalImage, ImageSize.SIZE_128.getSize());

            final String filePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioRepository.upload(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath,
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
            );
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading group chat photo in service " + e.getMessage());
        }
    }

    public InputStream download(@NonNull String filePath) {
        try {
            return minioRepository.download(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            throw new RuntimeException("Error downloading group chat photo in service " + e.getMessage());
        }
    }

    public void delete(@NonNull String filePath) {
        try {
            minioRepository.delete(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting group chat photo in service " + e.getMessage());
        }
    }

}

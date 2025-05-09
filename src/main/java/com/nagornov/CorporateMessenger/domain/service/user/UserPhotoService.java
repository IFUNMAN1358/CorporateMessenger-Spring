package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import com.nagornov.CorporateMessenger.domain.enums.minio.MinioBucket;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.utils.ContentTypeUtils;
import com.nagornov.CorporateMessenger.domain.utils.MinioUtils;
import com.nagornov.CorporateMessenger.domain.utils.ScalrUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserPhotoRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPhotoService {

    private final JpaUserPhotoRepository jpaUserPhotoRepository;
    private final MinioRepository minioRepository;

    @Transactional
    public UserPhoto upload(@NonNull UUID userId, @NonNull MultipartFile file) {
        try {
            ContentTypeUtils.validateAsImageFromContentType(file.getContentType());

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            String originalFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
            minioRepository.upload(MinioBucket.USER_PHOTOS, originalFilePath, originalImage, "jpg");

            BufferedImage smallImage = ScalrUtils.resizeImage(originalImage, ImageSize.SIZE_128);
            String smallFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
            minioRepository.upload(MinioBucket.USER_PHOTOS, smallFilePath, smallImage, "jpg");

            UserPhoto userPhoto = new UserPhoto(
                    UUID.randomUUID(),
                    userId,
                    file.getOriginalFilename(),
                    smallFilePath,
                    originalFilePath,
                    "jpg",
                    true,
                    Instant.now()
            );
            return jpaUserPhotoRepository.save(userPhoto);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    @Transactional
    public UserPhoto update(@NonNull UserPhoto userPhoto) {
        return jpaUserPhotoRepository.save(userPhoto);
    }

    @Transactional
    public List<UserPhoto> updateAll(@NonNull List<UserPhoto> userPhotos) {
        return jpaUserPhotoRepository.saveAll(userPhotos);
    }

    @Transactional
    public void delete(@NonNull UserPhoto userPhoto) {
        jpaUserPhotoRepository.delete(userPhoto);
    }

    @Transactional
    public void deleteById(@NonNull UUID id) {
        jpaUserPhotoRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByUserId(@NonNull UUID userId) {
        jpaUserPhotoRepository.deleteAllByUserId(userId);
    }

    public Resource download(@NonNull UserPhoto userPhoto, @NonNull String size) {
        try {
            InputStream inputStream;
            if (size.equals("big")) {
                inputStream = minioRepository.download(MinioBucket.USER_PHOTOS, userPhoto.getBigFilePath());
            } else if (size.equals("small")) {
                inputStream = minioRepository.download(MinioBucket.USER_PHOTOS, userPhoto.getSmallFilePath());
            } else {
                throw new ResourceBadRequestException("Invalid size param for downloading user photo");
            }
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    public Resource downloadByFilePath(@NonNull String filePath) {
        return new InputStreamResource(
                minioRepository.download(MinioBucket.USER_PHOTOS, filePath)
        );
    }

    public Optional<UserPhoto> findMainByUserId(@NonNull UUID userId) {
        return jpaUserPhotoRepository.findMainByUserId(userId);
    }

    public Optional<UserPhoto> findByIdAndUserId(@NonNull UUID id, @NonNull UUID userId) {
        return jpaUserPhotoRepository.findByIdAndUserId(id, userId);
    }

    public UserPhoto getMainByUserId(@NonNull UUID userId) {
        return jpaUserPhotoRepository.findMainByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPhoto[userId=%s] not found".formatted(userId)));
    }

    public UserPhoto getByIdAndUserId(@NonNull UUID id, @NonNull UUID userId) {
        return jpaUserPhotoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id and userId not found"));
    }

    public UserPhoto getById(@NonNull UUID id) {
        return jpaUserPhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id not found"));
    }

    public List<UserPhoto> findAllByUserId(@NonNull UUID userId) {
        return jpaUserPhotoRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
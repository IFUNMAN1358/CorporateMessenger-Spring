package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import com.nagornov.CorporateMessenger.domain.enums.minio.MinioBucket;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import com.nagornov.CorporateMessenger.domain.utils.ContentTypeUtils;
import com.nagornov.CorporateMessenger.domain.utils.MinioUtils;
import com.nagornov.CorporateMessenger.domain.utils.ScalrUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaEmployeePhotoRepository;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeePhotoService {

    private final JpaEmployeePhotoRepository jpaEmployeePhotoRepository;
    private final MinioRepository minioRepository;

    @Transactional
    public EmployeePhoto upload(@NonNull UUID employeeId, @NonNull MultipartFile file) {
        try {
            ContentTypeUtils.validateAsImageFromContentType(file.getOriginalFilename());

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            String originalFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
            minioRepository.upload(MinioBucket.EMPLOYEE_PHOTOS, originalFilePath, originalImage, "jpg");

            BufferedImage smallImage = ScalrUtils.resizeImage(originalImage, ImageSize.SIZE_128);
            String smallFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
            minioRepository.upload(MinioBucket.EMPLOYEE_PHOTOS, smallFilePath, smallImage, "jpg");

            EmployeePhoto employeePhoto = new EmployeePhoto(
                UUID.randomUUID(),
                employeeId,
                file.getOriginalFilename(),
                smallFilePath,
                originalFilePath,
                "jpg",
                Instant.now()
            );
            return jpaEmployeePhotoRepository.save(employeePhoto);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    @Transactional
    public void delete(@NonNull EmployeePhoto employeePhoto) {
        jpaEmployeePhotoRepository.delete(employeePhoto);
    }

    @Transactional
    public void deleteById(@NonNull UUID id) {
        jpaEmployeePhotoRepository.deleteById(id);
    }

    @Transactional
    public void deleteByEmployeeId(@NonNull UUID employeeId) {
        jpaEmployeePhotoRepository.deleteByEmployeeId(employeeId);
    }

    public Resource download(@NonNull EmployeePhoto employeePhoto, @NonNull String size) {
        try {
            InputStream inputStream;
            if (size.equals("big")) {
                inputStream = minioRepository.download(MinioBucket.EMPLOYEE_PHOTOS, employeePhoto.getBigFilePath());
            } else if (size.equals("small")) {
                inputStream = minioRepository.download(MinioBucket.EMPLOYEE_PHOTOS, employeePhoto.getSmallFilePath());
            } else {
                throw new ResourceBadRequestException("Invalid size param for downloading employee photo");
            }
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    public Resource downloadByFilePath(@NonNull String filePath) {
        return new InputStreamResource(
                minioRepository.download(MinioBucket.EMPLOYEE_PHOTOS, filePath)
        );
    }

    public Optional<EmployeePhoto> findById(@NonNull UUID id) {
        return jpaEmployeePhotoRepository.findById(id);
    }

    public Optional<EmployeePhoto> findByEmployeeId(@NonNull UUID employeeId) {
        return jpaEmployeePhotoRepository.findByEmployeeId(employeeId);
    }

}

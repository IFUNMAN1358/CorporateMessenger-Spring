package com.nagornov.CorporateMessenger.domain.service.message;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import com.nagornov.CorporateMessenger.domain.enums.minio.MinioBucket;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.domain.utils.ContentTypeUtils;
import com.nagornov.CorporateMessenger.domain.utils.MinioUtils;
import com.nagornov.CorporateMessenger.domain.utils.ScalrUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageFileRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
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
public class MessageFileService {

    private final CassandraMessageFileRepository cassandraMessageFileRepository;
    private final MinioRepository minioRepository;

    public MessageFile upload(@NonNull UUID messageId, @NonNull MultipartFile file) {
        try {
            String originalFilePath;
            String smallFilePath = null;
            if (ContentTypeUtils.isImage(file.getContentType())) {

                BufferedImage originalImage = ImageIO.read(file.getInputStream());
                originalFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
                minioRepository.upload(MinioBucket.MESSAGE_FILES, originalFilePath, originalImage, file.getContentType());

                BufferedImage smallImage = ScalrUtils.resizeImage(originalImage, ImageSize.SIZE_128);
                smallFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
                minioRepository.upload(MinioBucket.MESSAGE_FILES, smallFilePath, smallImage, file.getContentType());

            } else {

                originalFilePath = MinioUtils.generateFilePath(file.getOriginalFilename());
                minioRepository.upload(MinioBucket.MESSAGE_FILES, originalFilePath, file.getInputStream(), file.getSize(), file.getContentType());

            }

            MessageFile messageFile = new MessageFile(
                    Uuids.timeBased(),
                    messageId,
                    file.getOriginalFilename(),
                    smallFilePath,
                    originalFilePath,
                    file.getContentType(),
                    Instant.now()
            );
            return cassandraMessageFileRepository.save(messageFile);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    public MessageFile update(@NonNull MessageFile messageFile) {
        return cassandraMessageFileRepository.save(messageFile);
    }

    public void delete(@NonNull MessageFile messageFile) {
        cassandraMessageFileRepository.delete(messageFile);
    }

    public void deleteAllByMessageId(@NonNull UUID messageId) {
        cassandraMessageFileRepository.deleteAllByMessageId(messageId);
    }

    public Resource download(@NonNull MessageFile messageFile, @NonNull String size) {
        try {
            InputStream inputStream;
            if (!ContentTypeUtils.isImage(messageFile.getMimeType()) && size.equals("small")) {
                inputStream = minioRepository.download(MinioBucket.MESSAGE_FILES, messageFile.getBigFilePath());
            } else if (size.equals("small")) {
                inputStream = minioRepository.download(MinioBucket.MESSAGE_FILES, messageFile.getSmallFilePath());
            } else if (size.equals("big")) {
                inputStream = minioRepository.download(MinioBucket.MESSAGE_FILES, messageFile.getBigFilePath());
            } else {
                throw new ResourceBadRequestException("Invalid file size param for downloading message file");
            }
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            throw new ResourceBadRequestException(e.getMessage());
        }
    }

    public Resource downloadByFilePath(@NonNull String filePath) {
        return new InputStreamResource(
                minioRepository.download(MinioBucket.MESSAGE_FILES, filePath)
        );
    }

    public List<MessageFile> findAllByMessageId(@NonNull UUID messageId) {
        return cassandraMessageFileRepository.findAllByMessageId(messageId);
    }

    public Optional<MessageFile> findByIdAndMessageId(@NonNull UUID id, @NonNull UUID messageId) {
        return cassandraMessageFileRepository.findByIdAndMessageId(id, messageId);
    }

    public MessageFile getByIdAndMessageId(@NonNull UUID id, @NonNull UUID messageId) {
        return cassandraMessageFileRepository.findByIdAndMessageId(id, messageId)
                .orElseThrow(() -> new ResourceNotFoundException("MessageFile[id=%s, messageId=%s] not found".formatted(id, messageId)));
    }

}

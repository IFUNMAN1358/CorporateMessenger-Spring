package com.nagornov.CorporateMessenger.domain.factory;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.nagornov.CorporateMessenger.domain.model.MessageFile;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class MessageFileFactory {

    public static MessageFile createFromMultipartFile(@NotNull MultipartFile file) {
        final UUID timeUuid = Uuids.timeBased();
        return new MessageFile(
                timeUuid,
                null,
                file.getOriginalFilename(),
                timeUuid + "_" + file.getOriginalFilename(),
                file.getContentType(),
                Instant.now()
        );
    }

}

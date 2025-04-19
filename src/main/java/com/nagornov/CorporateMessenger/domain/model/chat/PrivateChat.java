package com.nagornov.CorporateMessenger.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrivateChat {

    private String userPairHash;
    private Long chatId;

    public static String generateUserPairHash(UUID userId1, UUID userId2) {
        List<String> ids = Arrays.asList(userId1.toString(), userId2.toString());
        Collections.sort(ids);
        String combined = "%s:%s".formatted(ids.getFirst(), ids.getLast());
        return DigestUtils.sha256Hex(combined);
    }

}

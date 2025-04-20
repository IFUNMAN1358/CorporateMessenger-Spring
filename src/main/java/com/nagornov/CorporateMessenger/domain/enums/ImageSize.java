package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum ImageSize {

    SIZE_128(128),
    SIZE_256(256),
    SIZE_512(512);

    private final int size;

    ImageSize(int size) {
        this.size = size;
    }

}

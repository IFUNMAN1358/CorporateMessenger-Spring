package com.nagornov.CorporateMessenger.domain.utils;

import com.nagornov.CorporateMessenger.domain.enums.ImageSize;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

@UtilityClass
public class ScalrUtils {

    public static BufferedImage resizeImage(@NonNull BufferedImage originalImage, @NonNull ImageSize imageSize) {
        return Scalr.resize(
            originalImage,
            Scalr.Method.QUALITY,
            Scalr.Mode.FIT_TO_WIDTH,
            imageSize.getSize(),
            Scalr.OP_ANTIALIAS
        );
    }

}

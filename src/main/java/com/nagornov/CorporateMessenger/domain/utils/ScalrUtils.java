package com.nagornov.CorporateMessenger.domain.utils;

import lombok.experimental.UtilityClass;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

@UtilityClass
public class ScalrUtils {

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
        return Scalr.resize(
            originalImage,
            Scalr.Method.QUALITY,
            Scalr.Mode.FIT_TO_WIDTH,
            targetSize,
            Scalr.OP_ANTIALIAS
        );
    }

}

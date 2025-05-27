package com.nagornov.CorporateMessenger.domain.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@UtilityClass
public class InputStreamUtils {

    public static BufferedImage inputStreamToBufferedImage(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            if (inputStream.available() == 0) {
                throw new IllegalArgumentException("InputStream is empty for file: " + file.getOriginalFilename());
            }
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IllegalArgumentException("Failed to read image from InputStream: unsupported format or corrupted data, contentType=" + file.getContentType());
            }
            return image;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert InputStream to BufferedImage: " + e.getMessage(), e);
        }
    }

    public static InputStream bufferedImageToInputStream(BufferedImage image, String mimeType) {
        try {
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                throw new IllegalArgumentException("Invalid BufferedImage: null or empty [width=" +
                        (image != null ? image.getWidth() : "null") + ", height=" +
                        (image != null ? image.getHeight() : "null") + "]");
            }

            String formatName = switch (mimeType.toLowerCase()) {
                case "image/jpeg", "image/jpg" -> "jpeg";
                case "image/png" -> "png";
                default -> throw new IllegalArgumentException("Unsupported image format: " + mimeType);
            };

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, outputStream);
            byte[] bytes = outputStream.toByteArray();

            if (bytes.length == 0) {
                throw new RuntimeException("Image data is empty after conversion");
            }

            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert BufferedImage to InputStream: " + e.getMessage(), e);
        }
    }

}

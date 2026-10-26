
package com.example.demo.media;

import com.example.demo.exception.ImageTooLargeException;
import com.example.demo.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalMediaStorage implements MediaStorage {

    private static final String PRODUCT_IMAGE_URL_PREFIX =
            "/media/products/";

    private final Path mediaDirectory;
    private final long maxFileSize;

    public LocalMediaStorage(
            @Value("${app.media.dir:C:/images}") String mediaDirectory,
            @Value("${app.upload.max-size:2097152}") long maxFileSize) {

        this.mediaDirectory = Path.of(mediaDirectory)
                .toAbsolutePath()
                .normalize();

        this.maxFileSize = maxFileSize;
    }


    @Override
    public String storeProductImage(Long productId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Image file is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new ImageTooLargeException(
                    "Image exceeds the maximum allowed size");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !(contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/webp"))) {
            throw new InvalidImageException(
                    "Only JPEG, PNG and WebP images are allowed");
        }

        try {
            byte[] bytes = file.getBytes();

            String extension = getValidatedExtension(bytes, contentType);

            Path productDirectory = mediaDirectory
                    .resolve("products")
                    .normalize();

            if (!productDirectory.startsWith(mediaDirectory)) {
                throw new InvalidImageException("Invalid image path");
            }

            Files.createDirectories(productDirectory);

            String filename = UUID.randomUUID() + extension;
            Path destination = productDirectory.resolve(filename).normalize();

            if (!destination.startsWith(productDirectory)) {
                throw new InvalidImageException("Invalid image path");
            }

            Files.write(destination, bytes);

            return PRODUCT_IMAGE_URL_PREFIX + filename;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not store product image", exception);
        }
    }

    private String getValidatedExtension(byte[] bytes, String contentType) {

        if (contentType.equals("image/jpeg")
                && hasJpegSignature(bytes)) {
            validateDecodableImage(bytes, "JPEG");
            return ".jpg";
        }

        if (contentType.equals("image/png")
                && hasPngSignature(bytes)) {
            validateDecodableImage(bytes, "PNG");
            return ".png";
        }

        if (contentType.equals("image/webp")
                && hasWebpSignature(bytes)) {
            validateWebpStructure(bytes);
            return ".webp";
        }

        throw new InvalidImageException(
                "File content does not match its image type");
    }

    private boolean hasJpegSignature(byte[] bytes) {
        return bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF;
    }

    private boolean hasPngSignature(byte[] bytes) {
        byte[] signature = {
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A
        };

        if (bytes.length < signature.length) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            if (bytes[i] != signature[i]) {
                return false;
            }
        }

        return true;
    }

    private boolean hasWebpSignature(byte[] bytes) {
        return bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P';
    }

    private void validateDecodableImage(byte[] bytes, String format) {
        try (ByteArrayInputStream input =
                     new ByteArrayInputStream(bytes)) {

            BufferedImage image = ImageIO.read(input);

            if (image == null
                    || image.getWidth() <= 0
                    || image.getHeight() <= 0) {
                throw new InvalidImageException(
                        "File is not a valid " + format + " image");
            }

        } catch (IOException exception) {
            throw new InvalidImageException(
                    "Could not decode " + format + " image");
        }
    }

    private void validateWebpStructure(byte[] bytes) {
        long declaredSize = Integer.toUnsignedLong(
                (bytes[4] & 0xFF)
                        | ((bytes[5] & 0xFF) << 8)
                        | ((bytes[6] & 0xFF) << 16)
                        | ((bytes[7] & 0xFF) << 24));

        if (declaredSize + 8 != bytes.length
                || bytes.length < 20) {
            throw new InvalidImageException(
                    "File has an invalid WebP structure");
        }
    }

    @Override
    public void deleteProductImage(String imageUrl) {

        if (imageUrl == null
                || !imageUrl.startsWith(PRODUCT_IMAGE_URL_PREFIX)) {
            return;
        }

        String filename = imageUrl.substring(
                PRODUCT_IMAGE_URL_PREFIX.length());

        if (filename.isBlank()
                || filename.contains("/")
                || filename.contains("\\")
                || filename.contains("..")) {
            throw new InvalidImageException("Invalid image path");
        }

        try {
            Path productDirectory = mediaDirectory
                    .resolve("products")
                    .normalize();

            Path imagePath = productDirectory
                    .resolve(filename)
                    .normalize();

            if (imagePath.startsWith(productDirectory)) {
                Files.deleteIfExists(imagePath);
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not delete product image", exception);
        }
    }
}

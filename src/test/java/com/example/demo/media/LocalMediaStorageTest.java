package com.example.demo.media;

import com.example.demo.exception.ImageTooLargeException;
import com.example.demo.exception.InvalidImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class LocalMediaStorageTest {

    @TempDir
    Path tempDir;

    private LocalMediaStorage storage;

    @BeforeEach
    void setUp() {
        storage = new LocalMediaStorage(
                tempDir.toString(),
                2 * 1024 * 1024
        );
    }

    @Test
    void storeProductImage_validPng_storesFileAndReturnsUrl()
            throws IOException {

        byte[] imageBytes = createPng();

        MultipartFile file = new MockMultipartFile(
                "file",
                "client-name.png",
                "image/png",
                imageBytes
        );

        String imageUrl = storage.storeProductImage( file);

        assertTrue(imageUrl.startsWith("/media/products/"));
        assertTrue(imageUrl.endsWith(".png"));

        String filename = imageUrl.substring(
                "/media/products/".length()
        );

        Path savedFile = tempDir.resolve("products").resolve(filename);

        assertTrue(Files.exists(savedFile));
        assertArrayEquals(imageBytes, Files.readAllBytes(savedFile));
        assertNotEquals("client-name.png", filename);
    }

    @Test
    void storeProductImage_generatesUniqueFilenames()
            throws IOException {

        MultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                createPng()
        );

        String firstUrl = storage.storeProductImage( file);
        String secondUrl = storage.storeProductImage( file);

        assertNotEquals(firstUrl, secondUrl);
    }

    @Test
    void storeProductImage_emptyFile_throwsInvalidImageException() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        assertThrows(
                InvalidImageException.class,
                () -> storage.storeProductImage( file)
        );

        assertFalse(Files.exists(tempDir.resolve("products")));
    }

    @Test
    void storeProductImage_unsupportedContentType_throwsException() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "not an image".getBytes()
        );

        assertThrows(
                InvalidImageException.class,
                () -> storage.storeProductImage( file)
        );

        assertFalse(Files.exists(tempDir.resolve("products")));
    }

    @Test
    void storeProductImage_contentTypeDoesNotMatchBytes_throwsException() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "fake.png",
                "image/png",
                "this is not a PNG".getBytes()
        );

        assertThrows(
                InvalidImageException.class,
                () -> storage.storeProductImage( file)
        );

        assertFalse(Files.exists(tempDir.resolve("products")));
    }

    @Test
    void storeProductImage_oversizedFile_throwsException() {

        byte[] bytes = new byte[1025];

        MultipartFile file = new MockMultipartFile(
                "file",
                "large.png",
                "image/png",
                bytes
        );

        storage = new LocalMediaStorage(tempDir.toString(), 1024);

        assertThrows(
                ImageTooLargeException.class,
                () -> storage.storeProductImage( file)
        );

        assertFalse(Files.exists(tempDir.resolve("products")));
    }

    @Test
    void deleteProductImage_existingFile_deletesFile()
            throws IOException {

        MultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                createPng()
        );

        String imageUrl = storage.storeProductImage( file);

        String filename = imageUrl.substring(
                "/media/products/".length()
        );

        Path savedFile = tempDir.resolve("products").resolve(filename);

        assertTrue(Files.exists(savedFile));

        storage.deleteProductImage(imageUrl);

        assertFalse(Files.exists(savedFile));
    }

    @Test
    void deleteProductImage_nullUrl_doesNothing() {
        assertDoesNotThrow(() -> storage.deleteProductImage(null));
    }

    @Test
    void deleteProductImage_invalidPath_throwsException() {
        assertThrows(
                InvalidImageException.class,
                () -> storage.deleteProductImage(
                        "/media/products/../application.properties"
                )
        );
    }

    private byte[] createPng() throws IOException {
        BufferedImage image = new BufferedImage(
                2,
                2,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        boolean written = ImageIO.write(image, "png", output);
        assertTrue(written);

        return output.toByteArray();
    }
}

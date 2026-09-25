package com.shivansh.cakes.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Handles local disk file storage for product, banner, and user avatar images.
 * In production this should be replaced with AWS S3 / Cloudinary (BRS §10.4).
 */
@Service
public class FileStorageService {

    private final Path baseUploadDir;

    public FileStorageService(@Value("${app.file.upload-dir:public/images}") String uploadDir) {
        this.baseUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * Saves the file under {@code subDir} (e.g. "product-img", "banner-img", "user-img").
     *
     * @return relative path suitable for storing in DB (e.g. "product-img/filename.jpg")
     */
    public String store(MultipartFile file, String subDir) {
        try {
            Path dir = baseUploadDir.resolve(subDir);
            Files.createDirectories(dir);

            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target);

            return subDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file: " + e.getMessage(), e);
        }
    }

    /** Deletes a file given its relative path (e.g. "product-img/abc.jpg"). */
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path target = baseUploadDir.resolve(relativePath).normalize();
            Files.deleteIfExists(target);
        } catch (IOException e) {
            // Log and continue — don't fail business operation on file deletion error
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}

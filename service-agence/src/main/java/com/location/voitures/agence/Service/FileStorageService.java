package com.location.voitures.agence.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageDirectory;
    private final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.storageDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException e) {
            logger.error("Could not create upload directory: {}", this.storageDirectory, e);
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path destinationFile = this.storageDirectory.resolve(uniqueFilename);
        try {
            file.transferTo(destinationFile);
            return uniqueFilename;
        } catch (IOException e) {
            logger.error("Failed to store file: {}", originalFilename, e);
            throw new RuntimeException("Failed to store file.", e);
        }
    }
    
    
    public void deleteFile(String filename) {
        Path fileToDeletePath = this.storageDirectory.resolve(Paths.get(filename)).normalize().toAbsolutePath();
        try {
            boolean deleted = Files.deleteIfExists(fileToDeletePath);
            if (deleted) {
                logger.info("File deleted successfully: {}", fileToDeletePath);
            } else {
                logger.warn("File not found, could not delete: {}", fileToDeletePath);
            }
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", fileToDeletePath, e);
            throw new RuntimeException("Failed to delete file.", e);
        }
    }

}
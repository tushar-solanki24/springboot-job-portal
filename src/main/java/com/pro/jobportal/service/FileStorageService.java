package com.pro.jobportal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> RESUME_TYPES = Set.of("application/pdf");
    private final Path uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();

    public String storeProfileImage(MultipartFile file) throws IOException {
        validateFile(file, IMAGE_TYPES, Set.of("jpg", "jpeg", "png", "webp"));
        return store(file);
    }

    public String storeResume(MultipartFile file) throws IOException {
        validateFile(file, RESUME_TYPES, Set.of("pdf"));
        return store(file);
    }

    private void validateFile(MultipartFile file, Set<String> allowedTypes, Set<String> allowedExtensions) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file content type");
        }

        String original = file.getOriginalFilename();
        String extension = getExtension(original);
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file extension");
        }
    }

    private String store(MultipartFile file) throws IOException {
        Files.createDirectories(uploadRoot);
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        Path target = uploadRoot.resolve(filename).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}

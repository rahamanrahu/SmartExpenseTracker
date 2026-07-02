package com.expense.ledger.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ReceiptStorageService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptStorageService.class);

    @Value("${ledger.storage.receipts-dir:./data/receipts}")
    private String receiptsDir;

    public String storeReceipt(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Receipt file is required");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !isValidReceiptType(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only images and PDFs are allowed");
        }

        // Create storage directory if it doesn't exist
        Path storageDir = Paths.get(receiptsDir);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String storedFilename = UUID.randomUUID().toString() + extension;
        Path targetPath = storageDir.resolve(storedFilename);

        // Store file
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Stored receipt: {}", storedFilename);
        return storedFilename;
    }

    private boolean isValidReceiptType(String contentType) {
        return contentType.startsWith("image/") || contentType.equals("application/pdf");
    }

    public void deleteReceipt(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }

        try {
            Path filePath = Paths.get(receiptsDir).resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("Deleted receipt: {}", filename);
        } catch (IOException e) {
            log.error("Failed to delete receipt: {}", filename, e);
        }
    }
}

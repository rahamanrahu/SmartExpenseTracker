package com.expense.ledger.controller;

import com.expense.ledger.service.ReceiptStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/receipt")
public class ReceiptController {

    private final ReceiptStorageService storageService;

    public ReceiptController(ReceiptStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadReceipt(@RequestParam("receipt") MultipartFile receiptFile) {
        try {
            String storedFilename = storageService.storeReceipt(receiptFile);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "filename", storedFilename,
                "originalName", receiptFile.getOriginalFilename(),
                "contentType", receiptFile.getContentType(),
                "size", receiptFile.getSize()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Upload failed: " + e.getMessage()
            ));
        }
    }
}

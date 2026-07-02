package com.expense.ledger.controller;

import com.expense.ledger.service.VoiceTranscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    private final VoiceTranscriptionService transcriptionService;

    public VoiceController(VoiceTranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @PostMapping("/transcribe")
    public ResponseEntity<Map<String, Object>> transcribeAudio(@RequestParam("audio") MultipartFile audioFile) {
        try {
            String transcription = transcriptionService.transcribeAudio(audioFile);
            
            // Parse transcription to extract expense details
            Map<String, Object> parsed = parseExpenseFromTranscription(transcription);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "transcription", transcription,
                "parsed", parsed
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Transcription failed: " + e.getMessage()
            ));
        }
    }

    private Map<String, Object> parseExpenseFromTranscription(String transcription) {
        // Simple keyword-based parsing
        String merchant = "";
        String amount = "";
        String category = "Other";
        
        String lower = transcription.toLowerCase();
        
        // Extract amount (look for numbers with currency symbols or decimal points)
        if (lower.contains("rupees") || lower.contains("rs") || lower.contains("₹")) {
            String[] words = transcription.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (words[i].matches(".*\\d+.*")) {
                    amount = words[i].replaceAll("[^0-9.]", "");
                    break;
                }
            }
        }
        
        // Detect category keywords
        if (lower.contains("food") || lower.contains("restaurant") || lower.contains("lunch") || lower.contains("dinner")) {
            category = "Food";
        } else if (lower.contains("transport") || lower.contains("uber") || lower.contains("taxi") || lower.contains("bus")) {
            category = "Transport";
        } else if (lower.contains("shopping") || lower.contains("store") || lower.contains("bought")) {
            category = "Shopping";
        } else if (lower.contains("bill") || lower.contains("utility") || lower.contains("electricity")) {
            category = "Bills";
        }
        
        return Map.of(
            "merchant", merchant,
            "amount", amount,
            "category", category,
            "note", transcription
        );
    }
}

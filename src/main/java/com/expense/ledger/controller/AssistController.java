package com.expense.ledger.controller;

import com.expense.ledger.service.AssistFileStorageService;
import com.expense.ledger.service.ReceiptAssistService;
import com.expense.ledger.service.VoiceTranscriptionService;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/assist")
public class AssistController {

   private final AssistFileStorageService storageService;
   private final ReceiptAssistService receiptAssistService;
   private final VoiceTranscriptionService voiceTranscriptionService;

   public AssistController(
      AssistFileStorageService storageService,
      ReceiptAssistService receiptAssistService,
      VoiceTranscriptionService voiceTranscriptionService
   ) {
      this.storageService = storageService;
      this.receiptAssistService = receiptAssistService;
      this.voiceTranscriptionService = voiceTranscriptionService;
   }

   @PostMapping("/receipt")
   public ResponseEntity<Map<String, Object>> receipt(
      @RequestParam("file") MultipartFile file,
      Authentication authentication
   ) {
      if (authentication == null || !authentication.isAuthenticated()) {
         return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not authenticated"));
      }
      if (file == null || file.isEmpty()) {
         return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No receipt file uploaded"));
      }
         String rawContentType = file.getContentType();
         String contentType = rawContentType == null ? "" : rawContentType.toLowerCase(Locale.ROOT);
      if (!contentType.startsWith("image/") && !"application/pdf".equals(contentType)) {
         return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Only image or PDF receipts are supported"));
      }

      try {
         String storedPath = storageService.store(file, "receipts");
         Map<String, Object> fields = receiptAssistService.suggestFromReceiptFile(file);
         Map<String, Object> body = new HashMap<>();
         body.put("success", true);
         body.put("storedPath", storedPath);
         body.put("fields", fields);
         return ResponseEntity.ok(body);
      } catch (Exception ex) {
         return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Receipt processing failed"));
      }
   }

   @PostMapping("/voice")
   public ResponseEntity<Map<String, Object>> voice(
      @RequestParam("file") MultipartFile file,
      Authentication authentication
   ) {
      if (authentication == null || !authentication.isAuthenticated()) {
         return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not authenticated"));
      }
      if (file == null || file.isEmpty()) {
         return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No audio file uploaded"));
      }

      try {
         String storedPath = storageService.store(file, "voice");
         Map<String, Object> transcriptRes = voiceTranscriptionService.transcribe(Path.of(storedPath), file.getOriginalFilename());
         String transcript = String.valueOf(transcriptRes.get("transcript"));
         Map<String, Object> fields = receiptAssistService.suggestFromTranscript(transcript);

         Map<String, Object> body = new HashMap<>();
         body.put("success", transcriptRes.get("success"));
         body.put("storedPath", storedPath);
         body.put("provider", transcriptRes.get("provider"));
         body.put("transcript", transcript);
         body.put("fields", fields);
         return ResponseEntity.ok(body);
      } catch (Exception ex) {
         return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Voice processing failed"));
      }
   }
}

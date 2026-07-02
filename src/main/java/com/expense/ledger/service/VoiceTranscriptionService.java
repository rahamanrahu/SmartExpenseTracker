package com.expense.ledger.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;

@Service
public class VoiceTranscriptionService {

    private static final Logger log = LoggerFactory.getLogger(VoiceTranscriptionService.class);

    @Value("${ledger.ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ledger.ai.groq.api-key:}")
    private String groqApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String transcribeAudio(MultipartFile audioFile) throws IOException {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("Audio file is required");
        }

        // Try OpenAI first, then Groq, then fallback
        if (openaiApiKey != null && !openaiApiKey.isBlank()) {
            try {
                return transcribeWithOpenAI(audioFile);
            } catch (Exception e) {
                log.warn("OpenAI transcription failed: {}", e.getMessage());
            }
        }

        if (groqApiKey != null && !groqApiKey.isBlank()) {
            try {
                return transcribeWithGroq(audioFile);
            } catch (Exception e) {
                log.warn("Groq transcription failed: {}", e.getMessage());
            }
        }

        return generateFallbackTranscription();
    }

    public Map<String, Object> transcribe(Path audioPath, String originalFilename) throws IOException {
        // This method is called by AssistController
        try {
            byte[] audioBytes = java.nio.file.Files.readAllBytes(audioPath);
            String transcript = transcribeAudioBytes(audioBytes, originalFilename);
            
            return Map.of(
                "success", true,
                "transcript", transcript,
                "provider", determineProvider()
            );
        } catch (Exception e) {
            log.error("Transcription failed", e);
            return Map.of(
                "success", false,
                "transcript", generateFallbackTranscription(),
                "provider", "fallback"
            );
        }
    }

    private String transcribeAudioBytes(byte[] audioBytes, String filename) throws IOException, InterruptedException {
        // Try OpenAI first, then Groq
        if (openaiApiKey != null && !openaiApiKey.isBlank()) {
            try {
                return transcribeWithOpenAIBytes(audioBytes, filename);
            } catch (Exception e) {
                log.warn("OpenAI transcription failed: {}", e.getMessage());
            }
        }

        if (groqApiKey != null && !groqApiKey.isBlank()) {
            try {
                return transcribeWithGroqBytes(audioBytes, filename);
            } catch (Exception e) {
                log.warn("Groq transcription failed: {}", e.getMessage());
            }
        }

        return generateFallbackTranscription();
    }

    private String determineProvider() {
        if (openaiApiKey != null && !openaiApiKey.isBlank()) return "openai";
        if (groqApiKey != null && !groqApiKey.isBlank()) return "groq";
        return "fallback";
    }

    private String transcribeWithOpenAI(MultipartFile audioFile) throws IOException, InterruptedException {
        byte[] audioBytes = audioFile.getBytes();
        return transcribeWithOpenAIBytes(audioBytes, audioFile.getOriginalFilename());
    }

    private String transcribeWithOpenAIBytes(byte[] audioBytes, String filename) throws IOException, InterruptedException {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        
        String body = buildMultipartBody(boundary, filename, audioBytes);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/audio/transcriptions"))
            .header("Authorization", "Bearer " + openaiApiKey)
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseTranscriptionResponse(response.body());
        }
        
        throw new IOException("OpenAI API returned status: " + response.statusCode());
    }

    private String transcribeWithGroq(MultipartFile audioFile) throws IOException, InterruptedException {
        byte[] audioBytes = audioFile.getBytes();
        return transcribeWithGroqBytes(audioBytes, audioFile.getOriginalFilename());
    }

    private String transcribeWithGroqBytes(byte[] audioBytes, String filename) throws IOException, InterruptedException {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        
        
        String body = buildMultipartBody(boundary, filename, audioBytes);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.groq.com/openai/v1/audio/transcriptions"))
            .header("Authorization", "Bearer " + groqApiKey)
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseTranscriptionResponse(response.body());
        }
        
        throw new IOException("Groq API returned status: " + response.statusCode());
    }

    private String buildMultipartBody(String boundary, String filename, byte[] audioBytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filename).append("\"\r\n");
        sb.append("Content-Type: audio/webm\r\n\r\n");
        sb.append(new String(audioBytes, StandardCharsets.ISO_8859_1));
        sb.append("\r\n--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"model\"\r\n\r\n");
        sb.append("whisper-1\r\n");
        sb.append("--").append(boundary).append("--\r\n");
        return sb.toString();
    }

    private String parseTranscriptionResponse(String responseBody) {
        // Simple JSON parsing for {"text": "..."}
        int textStart = responseBody.indexOf("\"text\"");
        if (textStart == -1) return responseBody;
        
        int colonIndex = responseBody.indexOf(":", textStart);
        int quoteStart = responseBody.indexOf("\"", colonIndex);
        int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);
        
        if (quoteStart != -1 && quoteEnd != -1) {
            return responseBody.substring(quoteStart + 1, quoteEnd);
        }
        
        return responseBody;
    }

    private String generateFallbackTranscription() {
        return "Voice transcription unavailable - please configure AI provider API keys";
    }
}

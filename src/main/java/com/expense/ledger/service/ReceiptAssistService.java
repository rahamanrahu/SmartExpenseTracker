package com.expense.ledger.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReceiptAssistService {

   private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?:rs\\.?|inr|₹)?\\s*(\\d+(?:[.,]\\d{1,2})?)", Pattern.CASE_INSENSITIVE);

   public Map<String, Object> suggestFromReceiptFile(MultipartFile file) {
      Map<String, Object> out = new HashMap<>();
      String originalName = file.getOriginalFilename();
      String name = originalName == null ? "" : originalName;
      String base = name.replaceAll("\\.[^.]+$", "").replace('_', ' ').trim();

      out.put("merchant", inferMerchant(base));
      out.put("category", inferCategory(base));
      out.put("date", LocalDate.now().toString());
      out.put("notes", "Imported from receipt " + name);

      BigDecimal amount = inferAmount(base);
      if (amount != null) {
         out.put("amount", amount.toPlainString());
      }
      return out;
   }

   public Map<String, Object> suggestFromTranscript(String transcript) {
      Map<String, Object> out = new HashMap<>();
      String text = transcript == null ? "" : transcript.trim();

      out.put("merchant", inferMerchant(text));
      out.put("category", inferCategory(text));
      out.put("date", LocalDate.now().toString());
      out.put("notes", text.isBlank() ? "Voice import" : text);

      BigDecimal amount = inferAmount(text);
      if (amount != null) {
         out.put("amount", amount.toPlainString());
      }
      return out;
   }

   private String inferMerchant(String text) {
      if (text == null || text.isBlank()) {
         return "";
      }
      String cleaned = text.replaceAll("\\d", " ").replaceAll("[^a-zA-Z ]", " ").trim();
      if (cleaned.isBlank()) {
         return "";
      }
      String[] parts = cleaned.split("\\s+");
      if (parts.length == 0) {
         return "";
      }
      return Character.toUpperCase(parts[0].charAt(0)) + parts[0].substring(1).toLowerCase(Locale.ROOT);
   }

   private String inferCategory(String text) {
      String t = text == null ? "" : text.toLowerCase(Locale.ROOT);
      if (t.contains("uber") || t.contains("bus") || t.contains("metro") || t.contains("cab")) return "transport";
      if (t.contains("swiggy") || t.contains("zomato") || t.contains("food") || t.contains("restaurant")) return "food";
      if (t.contains("amazon") || t.contains("flipkart") || t.contains("shop")) return "shopping";
      if (t.contains("bill") || t.contains("electric") || t.contains("recharge")) return "bills";
      if (t.contains("movie") || t.contains("netflix") || t.contains("spotify")) return "entertainment";
      if (t.contains("pharma") || t.contains("hospital") || t.contains("health")) return "health";
      return "other";
   }

   private BigDecimal inferAmount(String text) {
      if (text == null || text.isBlank()) {
         return null;
      }
      Matcher m = AMOUNT_PATTERN.matcher(text);
      BigDecimal best = null;
      while (m.find()) {
         String raw = m.group(1).replace(",", "");
         try {
            BigDecimal v = new BigDecimal(raw);
            if (best == null || v.compareTo(best) > 0) {
               best = v;
            }
         } catch (Exception ignored) {
         }
      }
      return best;
   }
}

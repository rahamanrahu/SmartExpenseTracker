package com.expense.ledger.service;

import com.expense.ledger.model.Budget;
import com.expense.ledger.model.Expense;
import com.expense.ledger.model.Goal;
import com.expense.ledger.model.User;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiService {
   private static final Logger log = LoggerFactory.getLogger(AiService.class);

   private static final String GEMINI_URL_TMPL =
      "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
   private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
   private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

   @Value("${ledger.ai.gemini.api-key:${GEMINI_API_KEY:}}")
   private String geminiApiKey;
   @Value("${ledger.ai.gemini.model:gemini-2.5-flash}")
   private String geminiModel;

   @Value("${ledger.ai.groq.api-key:${GROQ_API_KEY:}}")
   private String groqApiKey;
   @Value("${ledger.ai.groq.model:llama-3.3-70b-versatile}")
   private String groqModel;

   @Value("${spring.ai.openai.api-key:}")
   private String openAiApiKey;
   @Value("${ledger.ai.openai.model:gpt-4o-mini}")
   private String openAiModel;

   private final RestClient restClient = RestClient.builder().build();

   private static final String SYSTEM_PROMPT =
      "You are a concise personal finance coach. Reply with exactly 2-3 short, actionable sentences. "
      + "No greeting, no markdown, no bullet points, no caveats.";

   @Cacheable(value = "aiInsights", key = "#user != null ? #user.id : 0")
   public String generateInsight(User user, List<Expense> expenses, List<Budget> budgets, List<Goal> goals) {
      String prompt = buildPrompt(expenses, budgets, goals);
      String fallback = generateFallbackInsight(expenses);

      String viaGemini = tryGemini(prompt);
      if (viaGemini != null) return viaGemini;

      String viaGroq = tryGroq(prompt);
      if (viaGroq != null) return viaGroq;

      String viaOpenAI = tryOpenAI(prompt);
      if (viaOpenAI != null) return viaOpenAI;

      return fallback;
   }

   public String generateInsight(List<Expense> expenses) {
      return generateFallbackInsight(expenses);
   }

   /**
    * Provider status for the settings / status page. Values: "active" (key set) or "idle".
    */
   public Map<String, String> providerStatus() {
      return Map.of(
         "gemini", (geminiApiKey != null && !geminiApiKey.isBlank()) ? "active" : "idle",
         "groq",   (groqApiKey   != null && !groqApiKey.isBlank())   ? "active" : "idle"
      );
   }

   // ---------------------------------------------------------------
   // Provider implementations
   // ---------------------------------------------------------------

   private String tryGemini(String prompt) {
      if (geminiApiKey == null || geminiApiKey.isBlank()) return null;
      try {
         String url = String.format(GEMINI_URL_TMPL, geminiModel, geminiApiKey);
         Map<String, Object> payload = Map.of(
            "contents", List.of(Map.of(
               "role", "user",
               "parts", List.of(Map.of("text", SYSTEM_PROMPT + "\n\n" + prompt))
            )),
            "generationConfig", Map.of(
               "temperature", 0.3,
               "maxOutputTokens", 220
            )
         );
         @SuppressWarnings("unchecked")
         Map<String, Object> response = restClient.post()
            .uri(Objects.requireNonNull(url))
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .body(Objects.requireNonNull(payload))
            .retrieve()
            .body(Map.class);
         String text = extractGeminiContent(response);
         return (text != null && !text.isBlank()) ? text.trim() : null;
      } catch (Exception ex) {
         log.warn("Gemini call failed ({}): {}", geminiModel, ex.getMessage());
         return null;
      }
   }

   private String tryGroq(String prompt) {
      if (groqApiKey == null || groqApiKey.isBlank()) return null;
      return tryOpenAICompatible(GROQ_URL, groqApiKey, groqModel, prompt, "Groq");
   }

   private String tryOpenAI(String prompt) {
      if (openAiApiKey == null || openAiApiKey.isBlank()) return null;
      return tryOpenAICompatible(OPENAI_URL, openAiApiKey, openAiModel, prompt, "OpenAI");
   }

   private String tryOpenAICompatible(String url, String apiKey, String model, String prompt, String label) {
      try {
         Map<String, Object> payload = Map.of(
            "model", model,
            "temperature", 0.3,
            "max_tokens", 220,
            "messages", List.of(
               Map.of("role", "system", "content", SYSTEM_PROMPT),
               Map.of("role", "user",   "content", prompt)
            )
         );
         @SuppressWarnings("unchecked")
         Map<String, Object> response = restClient.post()
            .uri(Objects.requireNonNull(url))
            .header("Authorization", "Bearer " + apiKey)
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .body(Objects.requireNonNull(payload))
            .retrieve()
            .body(Map.class);
         String text = extractOpenAIContent(response);
         return (text != null && !text.isBlank()) ? text.trim() : null;
      } catch (Exception ex) {
         log.warn("{} call failed ({}): {}", label, model, ex.getMessage());
         return null;
      }
   }

   // ---------------------------------------------------------------
   // Fallback + prompt + parsing
   // ---------------------------------------------------------------

   public String generateFallbackInsight(List<Expense> expenses) {
      if (expenses == null || expenses.isEmpty()) {
         return "Start tracking your expenses to get personalized insights!";
      }
      Map<String, BigDecimal> categoryTotals = expenses.stream()
         .filter(e -> e.getAmount() != null && e.getCategory() != null)
         .collect(Collectors.groupingBy(
            Expense::getCategory,
            Collectors.reducing(BigDecimal.ZERO, e -> e.getAmount().abs(), BigDecimal::add)
         ));
      if (categoryTotals.isEmpty()) {
         return "Add more expense details to receive better insights.";
      }
      String topCategory = categoryTotals.entrySet().stream()
         .max(Entry.comparingByValue()).map(Entry::getKey).orElse("general");
      BigDecimal topAmount = categoryTotals.get(topCategory);
      BigDecimal total = categoryTotals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
      if (total.compareTo(BigDecimal.ZERO) == 0) {
         return "Your expense tracking is looking good!";
      }
      double percentage = topAmount.divide(total, 2, RoundingMode.HALF_UP)
         .multiply(BigDecimal.valueOf(100L)).doubleValue();
      return String.format(
         "Your highest spending is in %s (%.0f%% of total). Consider setting a budget to track this category better.",
         topCategory, percentage
      );
   }

   private String buildPrompt(List<Expense> expenses, List<Budget> budgets, List<Goal> goals) {
      LocalDate cutoff = LocalDate.now().minusDays(30L);
      List<Expense> expensesSafe = expenses == null ? List.<Expense>of() : expenses;
      Map<String, BigDecimal> categoryTotals = expensesSafe.stream()
         .filter(e -> e.getDate() != null && !e.getDate().isBefore(cutoff))
         .filter(e -> e.getAmount() != null && e.getCategory() != null)
         .collect(Collectors.groupingBy(
            e -> e.getCategory().toLowerCase(Locale.ROOT),
            Collectors.reducing(BigDecimal.ZERO, e -> e.getAmount().abs(), BigDecimal::add)
         ));
      String categorySummary = categoryTotals.entrySet().stream()
         .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
         .limit(8L)
         .map(e -> e.getKey() + ": " + e.getValue().toPlainString())
         .collect(Collectors.joining(", "));
      List<Budget> budgetsSafe = budgets == null ? List.<Budget>of() : budgets;
      String budgetSummary = budgetsSafe.stream().filter(Objects::nonNull).map(b -> {
         BigDecimal amount = b.getLimitAmount() == null ? BigDecimal.ZERO : b.getLimitAmount();
         return b.getCategory() + "(" + b.getPeriod() + "):" + amount.toPlainString();
      }).collect(Collectors.joining(", "));
      List<Goal> goalsSafe = goals == null ? List.<Goal>of() : goals;
      String goalSummary = goalsSafe.stream().filter(Objects::nonNull).map(g -> {
         BigDecimal current = g.getCurrentAmount() == null ? BigDecimal.ZERO : g.getCurrentAmount();
         BigDecimal target  = g.getTargetAmount()  == null ? BigDecimal.ZERO : g.getTargetAmount();
         return g.getName() + " " + current.toPlainString() + "/" + target.toPlainString();
      }).collect(Collectors.joining(", "));
      List<String> lines = new ArrayList<>();
      lines.add("Analyze this personal finance snapshot and give 2-3 actionable insights.");
      lines.add("30-day category totals: " + (categorySummary.isBlank() ? "none" : categorySummary));
      lines.add("Budgets: " + (budgetSummary.isBlank() ? "none" : budgetSummary));
      lines.add("Goals: "   + (goalSummary.isBlank()   ? "none" : goalSummary));
      lines.add("Keep response concise, practical, and non-judgmental.");
      return String.join("\n", lines);
   }

   private String extractOpenAIContent(Map<String, Object> response) {
      if (response == null) return null;
      Object choicesObj = response.get("choices");
      if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) return null;
      Object first = choices.get(0);
      if (!(first instanceof Map<?, ?> choiceMap)) return null;
      Object msg = choiceMap.get("message");
      if (!(msg instanceof Map<?, ?> messageMap)) return null;
      Object content = messageMap.get("content");
      return (content instanceof String s) ? s : null;
   }

   private String extractGeminiContent(Map<String, Object> response) {
      if (response == null) return null;
      Object candidatesObj = response.get("candidates");
      if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) return null;
      Object first = candidates.get(0);
      if (!(first instanceof Map<?, ?> candidate)) return null;
      Object contentObj = candidate.get("content");
      if (!(contentObj instanceof Map<?, ?> content)) return null;
      Object partsObj = content.get("parts");
      if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) return null;
      StringBuilder sb = new StringBuilder();
      for (Object p : parts) {
         if (p instanceof Map<?, ?> partMap) {
            Object text = partMap.get("text");
            if (text instanceof String s) sb.append(s);
         }
      }
      return sb.isEmpty() ? null : sb.toString();
   }
}

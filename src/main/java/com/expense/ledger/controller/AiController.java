package com.expense.ledger.controller;

import com.expense.ledger.model.User;
import com.expense.ledger.service.AiService;
import com.expense.ledger.service.BudgetService;
import com.expense.ledger.service.ExpenseService;
import com.expense.ledger.service.GoalService;
import com.expense.ledger.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry points used by the dashboard JS to request fresh AI insights
 * on demand (refresh button, Pro features, etc.).
 *
 * CSRF is disabled for /ai/** (see SecurityConfig) so these can be called
 * as plain JSON without a token. Authentication is still required.
 */
@RestController
@RequestMapping("/ai")
public class AiController {

   private static final Logger log = LoggerFactory.getLogger(AiController.class);

   private final AiService aiService;
   private final UserService userService;
   private final ExpenseService expenseService;
   private final BudgetService budgetService;
   private final GoalService goalService;

   public AiController(AiService aiService,
                       UserService userService,
                       ExpenseService expenseService,
                       BudgetService budgetService,
                       GoalService goalService) {
      this.aiService = aiService;
      this.userService = userService;
      this.expenseService = expenseService;
      this.budgetService = budgetService;
      this.goalService = goalService;
   }

   @GetMapping("/status")
   public ResponseEntity<Map<String, Object>> status() {
      Map<String, Object> body = new HashMap<>();
      body.put("providers", aiService.providerStatus());
      body.put("enabled", aiService.providerStatus().values().stream().anyMatch("active"::equals));
      return ResponseEntity.ok(body);
   }

   @PostMapping("/insight")
   public ResponseEntity<Map<String, Object>> insight(Authentication auth) {
      User user = userService.getCurrentUser(auth);
      if (user == null) {
         return ResponseEntity.status(401).body(Map.of("error", "not-authenticated"));
      }
      try {
         String text = aiService.generateInsight(
            user,
            expenseService.getAllExpenses(user),
            budgetService.getBudgets(user),
            goalService.getGoals(user)
         );
         Map<String, Object> body = new HashMap<>();
         body.put("insight", text);
         body.put("providers", aiService.providerStatus());
         return ResponseEntity.ok(body);
      } catch (Exception ex) {
         log.warn("AI insight failed for user {}: {}", user.getId(), ex.getMessage());
         return ResponseEntity.ok(Map.of("insight", aiService.generateFallbackInsight(null),
                                          "fallback", true));
      }
   }
}

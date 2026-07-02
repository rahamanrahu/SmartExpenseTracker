package com.expense.ledger.service;

import com.expense.ledger.model.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

   private final UserService userService;

   public BillingService(UserService userService) {
      this.userService = userService;
   }

   @Transactional
   public Map<String, Object> upgradeToPro(User user) {
      Map<String, Object> result = new HashMap<>();
      if (user == null) {
         result.put("success", false);
         result.put("message", "User is not authenticated.");
         return result;
      }

      String currentPlan = user.getPlanName() == null || user.getPlanName().isBlank() ? "Free" : user.getPlanName();
      if ("Pro".equalsIgnoreCase(currentPlan)) {
         result.put("success", true);
         result.put("message", "You are already on Pro.");
         result.put("plan", "Pro");
         return result;
      }

      user.setPlanName("Pro");
      user.setPlanUpdatedAt(LocalDateTime.now());
      userService.save(user);

      result.put("success", true);
      result.put("message", "Upgrade complete. Welcome to Pro.");
      result.put("plan", "Pro");
      return result;
   }
}

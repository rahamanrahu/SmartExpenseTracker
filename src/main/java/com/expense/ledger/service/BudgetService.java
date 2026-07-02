package com.expense.ledger.service;

import com.expense.ledger.dto.BudgetRequest;
import com.expense.ledger.model.Budget;
import com.expense.ledger.model.User;
import com.expense.ledger.repository.BudgetRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {
   private static final Logger log = LoggerFactory.getLogger(BudgetService.class);
   private final BudgetRepository budgetRepository;

   public BudgetService(BudgetRepository budgetRepository) {
      this.budgetRepository = budgetRepository;
   }

   @Transactional
   public Budget createBudget(BudgetRequest request, User user) {
      try {
         if (request != null && user != null) {
            String category = this.normalizeCategory(request.getCategory());
            BigDecimal amount = this.normalizeAmount(request.getAmount());
            Budget budget = this.budgetRepository.findByUserAndCategoryIgnoreCase(user, category).orElseGet(Budget::new);
            budget.setUser(user);
            budget.setCategory(category);
            budget.setLimitAmount(amount);
            budget.setPeriod(this.normalizePeriod(request.getPeriod()));
            return (Budget)this.budgetRepository.save(budget);
         } else {
            return new Budget();
         }
      } catch (Exception var6) {
         log.error("Failed to save budget for user {}", user != null ? user.getEmail() : "unknown", var6);
         return new Budget();
      }
   }

   @Transactional(readOnly = true)
   public List<Budget> getBudgets(User user) {
      try {
         return this.budgetRepository.findByUserOrderByIdDesc(user);
      } catch (Exception var3) {
         log.error("Failed to load budgets for user {}", user != null ? user.getEmail() : "unknown", var3);
         return Collections.emptyList();
      }
   }

   public BigDecimal totalBudget(User user) {
      return this.getBudgets(user).stream().map(Budget::getLimitAmount).filter(amount -> amount != null).reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   public BigDecimal totalBudget(User user, String period) {
      String normalized = this.normalizePeriod(period);
      return this.getBudgets(user)
         .stream()
         .filter(b -> this.normalizePeriod(b.getPeriod()).equals(normalized))
         .map(Budget::getLimitAmount)
         .filter(amount -> amount != null)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   @Transactional
   public void deleteBudget(Long id, User user) {
      try {
         if (id == null || user == null) {
            return;
         }

         this.budgetRepository.findById(id).filter(budget -> this.isOwnedByUser(budget, user)).ifPresent(this.budgetRepository::delete);
      } catch (Exception var4) {
         log.error("Failed to delete budget {} for user {}", new Object[]{id, user != null ? user.getEmail() : "unknown", var4});
      }
   }

   @Transactional
   public Budget updateBudget(Long id, BudgetRequest request, User user) {
      try {
         if (id != null && request != null && user != null) {
            Budget budget = this.budgetRepository.findById(id).filter(b -> this.isOwnedByUser(b, user)).orElseGet(Budget::new);
            if (budget.getId() == null) {
               return new Budget();
            } else {
               budget.setUser(user);
               budget.setCategory(this.normalizeCategory(request.getCategory()));
               budget.setLimitAmount(this.normalizeAmount(request.getAmount()));
               budget.setPeriod(this.normalizePeriod(request.getPeriod()));
               return (Budget)this.budgetRepository.save(budget);
            }
         } else {
            return new Budget();
         }
      } catch (Exception var5) {
         log.error("Failed to update budget {} for user {}", new Object[]{id, user != null ? user.getEmail() : "unknown", var5});
         return new Budget();
      }
   }

   private boolean isOwnedByUser(Budget budget, User user) {
      return budget != null
         && budget.getUser() != null
         && budget.getUser().getId() != null
         && user.getId() != null
         && budget.getUser().getId().equals(user.getId());
   }

   private String normalizeCategory(String category) {
      return category != null && !category.isBlank() ? category.trim().toLowerCase(Locale.ROOT) : "other";
   }

   private BigDecimal normalizeAmount(BigDecimal amount) {
      return amount == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : amount.setScale(2, RoundingMode.HALF_UP);
   }

   private String normalizePeriod(String period) {
      if (period != null && !period.isBlank()) {
         String normalized = period.trim().toLowerCase(Locale.ROOT);

         return switch (normalized) {
            case "weekly", "yearly" -> normalized;
            default -> "monthly";
         };
      } else {
         return "monthly";
      }
   }
}

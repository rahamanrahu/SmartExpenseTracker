package com.expense.ledger.service;

import com.expense.ledger.dto.ExpenseRequest;
import com.expense.ledger.model.Expense;
import com.expense.ledger.model.User;
import com.expense.ledger.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {
   private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);
   private final ExpenseRepository expenseRepository;

   public ExpenseService(ExpenseRepository expenseRepository) {
      this.expenseRepository = expenseRepository;
   }

   @Transactional
   public Expense addExpense(ExpenseRequest request, User user) {
      try {
         if (request != null && user != null) {
            Expense expense = this.resolveExpense(request.getId(), user).orElseGet(Expense::new);
            Expense saved = (Expense)this.expenseRepository.save(Objects.requireNonNull(this.applyExpenseRequest(expense, request, user)));
            return saved == null ? new Expense() : saved;
         } else {
            return new Expense();
         }
      } catch (Exception var5) {
         log.error("Failed to save expense for user {}", user != null ? user.getEmail() : "unknown", var5);
         return new Expense();
      }
   }

   @Transactional
   public Expense updateExpense(Long id, ExpenseRequest request, User user) {
      try {
         if (id != null && request != null && user != null) {
            Optional<Expense> existing = this.resolveExpense(id, user);
            if (existing.isEmpty()) {
               return new Expense();
            } else {
               request.setId(id);
               Expense saved = (Expense)this.expenseRepository.save(Objects.requireNonNull(this.applyExpenseRequest(existing.get(), request, user)));
               return saved == null ? new Expense() : saved;
            }
         } else {
            return new Expense();
         }
      } catch (Exception var6) {
         log.error("Failed to update expense {} for user {}", new Object[]{id, user != null ? user.getEmail() : "unknown", var6});
         return new Expense();
      }
   }

   private String normalizeMerchant(String merchant) {
      return merchant != null && !merchant.isBlank() ? merchant.trim() : "Unknown merchant";
   }

   private String normalizeCategory(String category) {
      return category != null && !category.isBlank() ? category.trim().toLowerCase(Locale.ROOT) : "other";
   }

   @Transactional(readOnly = true)
   public List<Expense> getAllExpenses(User user) {
      try {
         return this.expenseRepository.findByUserOrderByDateDesc(user);
      } catch (Exception var3) {
         log.error("Failed to load expenses for user {}", user != null ? user.getEmail() : "unknown", var3);
         return Collections.emptyList();
      }
   }

   @Transactional
   public void deleteExpense(Long id, User user) {
      try {
         this.resolveExpense(id, user).ifPresent(this.expenseRepository::delete);
      } catch (Exception var4) {
         log.error("Failed to delete expense {} for user {}", new Object[]{id, user != null ? user.getEmail() : "unknown", var4});
      }
   }

   @Transactional(readOnly = true)
   public Optional<Expense> resolveExpense(Long id, User user) {
      if (id == null) {
         return Optional.empty();
      } else {
         try {
            return this.expenseRepository.findByIdAndUser(id, user);
         } catch (Exception var4) {
            log.error("Failed to resolve expense {} for user {}", new Object[]{id, user != null ? user.getEmail() : "unknown", var4});
            return Optional.empty();
         }
      }
   }

   public BigDecimal getTotalSpent(User user) {
      return this.getAllExpenses(user)
         .stream()
         .map(Expense::getAmount)
         .filter(amount -> amount != null)
         .map(BigDecimal::abs)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   public BigDecimal getSpentForPeriod(User user, String period) {
      LocalDate end = LocalDate.now();
      String var5 = this.normalizePeriod(period);

      LocalDate start = switch (var5) {
         case "weekly" -> end.with(DayOfWeek.MONDAY);
         case "yearly" -> end.withDayOfYear(1);
         default -> end.withDayOfMonth(1);
      };
      return this.getAllExpenses(user)
         .stream()
         .filter(e -> e.getDate() != null)
         .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
         .map(Expense::getAmount)
         .filter(amount -> amount != null)
         .map(BigDecimal::abs)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   public List<Expense> getRecentExpenses(User user, int limit) {
      List<Expense> all = this.getAllExpenses(user);
      return all.size() <= limit ? all : all.subList(0, limit);
   }

   private Expense applyExpenseRequest(Expense expense, ExpenseRequest request, User user) {
      expense.setUser(user);
      expense.setMerchant(this.normalizeMerchant(request.getMerchant()));
      expense.setCategory(this.normalizeCategory(request.getCategory()));
      expense.setDate(request.getDate());
      expense.setNote(request.getNotes());
      expense.setTags(request.getTags());
      BigDecimal amount = request.getAmount() == null ? BigDecimal.ZERO : request.getAmount();
      expense.setAmount(amount.abs().setScale(2, RoundingMode.HALF_UP).negate());
      return expense;
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

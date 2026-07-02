package com.expense.ledger.service;

import com.expense.ledger.dto.GoalRequest;
import com.expense.ledger.model.Goal;
import com.expense.ledger.model.User;
import com.expense.ledger.repository.GoalRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoalService {
   private static final Logger log = LoggerFactory.getLogger(GoalService.class);
   private final GoalRepository goalRepository;

   public GoalService(GoalRepository goalRepository) {
      this.goalRepository = goalRepository;
   }

   @Transactional
   public Goal createGoal(GoalRequest request, User user) {
      try {
         Goal goal = new Goal();
         goal.setUser(user);
         goal.setName(request.getName().trim());
         goal.setEmoji(request.getEmoji() != null && !request.getEmoji().isBlank() ? request.getEmoji().trim() : "\ud83c\udfaf");
         goal.setTargetAmount(request.getTargetAmount().setScale(2, RoundingMode.HALF_UP));
         BigDecimal current = request.getCurrentAmount() == null ? BigDecimal.ZERO : request.getCurrentAmount();
         goal.setCurrentAmount(current.setScale(2, RoundingMode.HALF_UP));
         if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
            try {
               goal.setDeadline(LocalDate.parse(request.getDeadline()));
            } catch (Exception var6) {
            }
         }

         return (Goal)this.goalRepository.save(goal);
      } catch (Exception var7) {
         log.error("Failed to save goal for user {}", user != null ? user.getEmail() : "unknown", var7);
         return new Goal();
      }
   }

   @Transactional(readOnly = true)
   public List<Goal> getGoals(User user) {
      try {
         return this.goalRepository.findByUserOrderByIdDesc(user);
      } catch (Exception var3) {
         log.error("Failed to load goals for user {}", user != null ? user.getEmail() : "unknown", var3);
         return Collections.emptyList();
      }
   }

   @Transactional
   public void deleteGoal(Long id, User user) {
      try {
         if (id == null || user == null) {
            return;
         }

         this.findByIdAndUser(id, user).ifPresent(this.goalRepository::delete);
      } catch (Exception var4) {
         log.error("Failed to delete goal {} for user {}", new Object[]{id, user != null ? user.getEmail() : "unknown", var4});
      }
   }

   @Transactional(readOnly = true)
   public Optional<Goal> findByIdAndUser(Long id, User user) {
      return this.goalRepository.findByIdAndUser(id, user);
   }

   @Transactional
   public Goal updateGoal(Long id, GoalRequest request, User user) {
      try {
         if (id != null && request != null && user != null) {
            Goal goal = this.findByIdAndUser(id, user).orElseGet(Goal::new);
            if (goal.getId() == null) {
               return new Goal();
            } else {
               goal.setName(request.getName().trim());
               goal.setEmoji(request.getEmoji() != null && !request.getEmoji().isBlank() ? request.getEmoji().trim() : "\ud83c\udfaf");
               goal.setTargetAmount(request.getTargetAmount().setScale(2, RoundingMode.HALF_UP));
               BigDecimal current = request.getCurrentAmount() == null ? BigDecimal.ZERO : request.getCurrentAmount();
               goal.setCurrentAmount(current.setScale(2, RoundingMode.HALF_UP));
               if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
                  try {
                     goal.setDeadline(LocalDate.parse(request.getDeadline()));
                  } catch (Exception var7) {
                     goal.setDeadline(null);
                  }
               } else {
                  goal.setDeadline(null);
               }

               return (Goal)this.goalRepository.save(goal);
            }
         } else {
            return new Goal();
         }
      } catch (Exception var8) {
         log.error("Failed to update goal {} for user {}", new Object[]{id, user != null ? user.getEmail() : "unknown", var8});
         return new Goal();
      }
   }

   @Transactional
   public boolean contribute(Long id, BigDecimal amount, User user) {
      return id != null && amount != null && amount.compareTo(BigDecimal.ZERO) > 0 && user != null ? this.findByIdAndUser(id, user).map(goal -> {
         BigDecimal target = goal.getTargetAmount() == null ? BigDecimal.ZERO : goal.getTargetAmount();
         BigDecimal current = goal.getCurrentAmount() == null ? BigDecimal.ZERO : goal.getCurrentAmount();
         BigDecimal newCurrent = current.add(amount).min(target);
         goal.setCurrentAmount(newCurrent.setScale(2, RoundingMode.HALF_UP));
         this.goalRepository.save(goal);
         return newCurrent.compareTo(target) >= 0;
      }).orElse(false) : false;
   }
}

package com.expense.ledger.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class GoalRequest {
   @NotBlank
   private String name;
   private String emoji;
   @NotNull
   @Min(0L)
   private BigDecimal targetAmount;
   @NotNull
   @Min(0L)
   private BigDecimal currentAmount = BigDecimal.ZERO;
   private String deadline;

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmoji() {
      return this.emoji;
   }

   public void setEmoji(String emoji) {
      this.emoji = emoji;
   }

   public BigDecimal getTargetAmount() {
      return this.targetAmount;
   }

   public void setTargetAmount(BigDecimal targetAmount) {
      this.targetAmount = targetAmount;
   }

   public BigDecimal getCurrentAmount() {
      return this.currentAmount;
   }

   public void setCurrentAmount(BigDecimal currentAmount) {
      this.currentAmount = currentAmount;
   }

   public String getDeadline() {
      return this.deadline;
   }

   public void setDeadline(String deadline) {
      this.deadline = deadline;
   }
}

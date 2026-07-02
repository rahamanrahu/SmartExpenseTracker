package com.expense.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class BudgetRequest {
   @NotBlank
   private String category;
   @NotNull(message = "Budget amount is required")
   @Positive(message = "Budget amount must be greater than zero")
   private BigDecimal amount;
   @NotBlank(message = "Period is required")
   private String period;

   public String getCategory() {
      return this.category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public BigDecimal getAmount() {
      return this.amount;
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   public String getPeriod() {
      return this.period;
   }

   public void setPeriod(String period) {
      this.period = period;
   }
}

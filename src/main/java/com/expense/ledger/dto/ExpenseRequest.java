package com.expense.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {
   private Long id;
   @NotBlank
   private String merchant;
   @NotNull(message = "Amount is required")
   @Positive(message = "Amount must be greater than zero")
   private BigDecimal amount;
   @NotBlank
   private String category;
   @NotNull
   private LocalDate date;
   private String account;
   @Size(max = 500)
   private String notes;
   @Size(max = 500)
   private String tags;
   private Boolean recurring = false;

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getMerchant() {
      return this.merchant;
   }

   public void setMerchant(String merchant) {
      this.merchant = merchant;
   }

   public BigDecimal getAmount() {
      return this.amount;
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   public String getCategory() {
      return this.category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public LocalDate getDate() {
      return this.date;
   }

   public void setDate(LocalDate date) {
      this.date = date;
   }

   public String getAccount() {
      return this.account;
   }

   public void setAccount(String account) {
      this.account = account;
   }

   public String getNotes() {
      return this.notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public String getTags() {
      return this.tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public Boolean getRecurring() {
      return this.recurring;
   }

   public void setRecurring(Boolean recurring) {
      this.recurring = recurring;
   }
}

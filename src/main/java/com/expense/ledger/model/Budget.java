package com.expense.ledger.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "budget")
public class Budget {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Column(nullable = false)
   private String category;
   @Column(nullable = false, precision = 12, scale = 2)
   private BigDecimal limitAmount;
   @Column(nullable = false)
   private String period = "monthly";
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getCategory() {
      return this.category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public BigDecimal getLimitAmount() {
      return this.limitAmount;
   }

   public void setLimitAmount(BigDecimal limitAmount) {
      this.limitAmount = limitAmount;
   }

   public String getPeriod() {
      return this.period;
   }

   public void setPeriod(String period) {
      this.period = period == null ? "monthly" : period;
   }

   public User getUser() {
      return this.user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}

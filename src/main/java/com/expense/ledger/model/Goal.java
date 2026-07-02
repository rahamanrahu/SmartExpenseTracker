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
import java.time.LocalDate;

@Entity
@Table(name = "goal")
public class Goal {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Column(nullable = false)
   private String name;
   @Column(length = 10)
   private String emoji;
   @Column(nullable = false, precision = 12, scale = 2)
   private BigDecimal targetAmount;
   @Column(nullable = false, precision = 12, scale = 2)
   private BigDecimal currentAmount;
   @Column
   private LocalDate deadline;
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
   }

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

   public LocalDate getDeadline() {
      return this.deadline;
   }

   public void setDeadline(LocalDate deadline) {
      this.deadline = deadline;
   }

   public User getUser() {
      return this.user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}

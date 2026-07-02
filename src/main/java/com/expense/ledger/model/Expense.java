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
@Table(name = "expense")
public class Expense {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Column(nullable = false)
   private String merchant;
   @Column(nullable = false, precision = 12, scale = 2)
   private BigDecimal amount;
   @Column(nullable = false)
   private String category;
   @Column(nullable = false)
   private LocalDate date;
   @Column(length = 500)
   private String note;
   @Column(length = 500)
   private String tags;
   @Column(length = 255)
   private String receiptFileName;
   @Column(length = 120)
   private String receiptContentType;
   @Column(length = 500)
   private String receiptStoredPath;
   @Column(length = 2000)
   private String voiceTranscript;
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

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

   public String getNote() {
      return this.note;
   }

   public void setNote(String note) {
      this.note = note;
   }

   public String getTags() {
      return this.tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public String getReceiptFileName() {
      return this.receiptFileName;
   }

   public void setReceiptFileName(String receiptFileName) {
      this.receiptFileName = receiptFileName;
   }

   public String getReceiptContentType() {
      return this.receiptContentType;
   }

   public void setReceiptContentType(String receiptContentType) {
      this.receiptContentType = receiptContentType;
   }

   public String getReceiptStoredPath() {
      return this.receiptStoredPath;
   }

   public void setReceiptStoredPath(String receiptStoredPath) {
      this.receiptStoredPath = receiptStoredPath;
   }

   public String getVoiceTranscript() {
      return this.voiceTranscript;
   }

   public void setVoiceTranscript(String voiceTranscript) {
      this.voiceTranscript = voiceTranscript;
   }

   public User getUser() {
      return this.user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}

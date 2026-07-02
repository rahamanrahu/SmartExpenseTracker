package com.expense.ledger.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "app_user")
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Column(nullable = false)
   private String name;
   @Column(nullable = false, unique = true)
   private String email;
   @Column(nullable = false)
   private String password;
   @Column(nullable = false)
   private boolean twoFactorEnabled = false;
   @Column(length = 64)
   private String twoFactorSecret;
   @Column(length = 20)
   private String phone;
   @Column(length = 60)
   private String timezone = "Asia/Kolkata";
   @Column(length = 10)
   private String theme = "dark";
   @Column(length = 10)
   private String currency = "INR";
   @Column(length = 10)
   private String language = "en-IN";
   @Column(nullable = false)
   private boolean budgetAlertsEnabled = true;
   @Column(nullable = false)
   private boolean weeklyDigestEnabled = true;
   @Column(nullable = false)
   private boolean aiInsightsEnabled = true;
   @Column(nullable = false)
   private boolean marketingEmailsEnabled = false;
   @Column(length = 20)
   private String planName = "Free";
   @Column
   private LocalDateTime planUpdatedAt;

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

   public String getEmail() {
      return this.email;
   }

   public void setEmail(String email) {
      this.email = email == null ? null : email.toLowerCase(Locale.ROOT).trim();
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public boolean isTwoFactorEnabled() {
      return this.twoFactorEnabled;
   }

   public void setTwoFactorEnabled(boolean twoFactorEnabled) {
      this.twoFactorEnabled = twoFactorEnabled;
   }

   public String getTwoFactorSecret() {
      return this.twoFactorSecret;
   }

   public void setTwoFactorSecret(String twoFactorSecret) {
      this.twoFactorSecret = twoFactorSecret;
   }

   public String getPhone() {
      return this.phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public String getTimezone() {
      return this.timezone;
   }

   public void setTimezone(String timezone) {
      this.timezone = timezone;
   }

   public String getTheme() {
      return this.theme;
   }

   public void setTheme(String theme) {
      this.theme = theme;
   }

   public String getCurrency() {
      return this.currency;
   }

   public void setCurrency(String currency) {
      this.currency = currency;
   }

   public String getLanguage() {
      return this.language;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public boolean isBudgetAlertsEnabled() {
      return this.budgetAlertsEnabled;
   }

   public void setBudgetAlertsEnabled(boolean budgetAlertsEnabled) {
      this.budgetAlertsEnabled = budgetAlertsEnabled;
   }

   public boolean isWeeklyDigestEnabled() {
      return this.weeklyDigestEnabled;
   }

   public void setWeeklyDigestEnabled(boolean weeklyDigestEnabled) {
      this.weeklyDigestEnabled = weeklyDigestEnabled;
   }

   public boolean isAiInsightsEnabled() {
      return this.aiInsightsEnabled;
   }

   public void setAiInsightsEnabled(boolean aiInsightsEnabled) {
      this.aiInsightsEnabled = aiInsightsEnabled;
   }

   public boolean isMarketingEmailsEnabled() {
      return this.marketingEmailsEnabled;
   }

   public void setMarketingEmailsEnabled(boolean marketingEmailsEnabled) {
      this.marketingEmailsEnabled = marketingEmailsEnabled;
   }

   public String getPlanName() {
      return this.planName;
   }

   public void setPlanName(String planName) {
      this.planName = planName;
   }

   public LocalDateTime getPlanUpdatedAt() {
      return this.planUpdatedAt;
   }

   public void setPlanUpdatedAt(LocalDateTime planUpdatedAt) {
      this.planUpdatedAt = planUpdatedAt;
   }

   @Transient
   public String getInitials() {
      if (this.name != null && !this.name.isBlank()) {
         String[] parts = this.name.trim().split("\\s+");
         return parts.length == 1
            ? parts[0].substring(0, 1).toUpperCase(Locale.ROOT)
            : (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
      } else {
         return "U";
      }
   }
}

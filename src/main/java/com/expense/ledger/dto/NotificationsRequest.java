package com.expense.ledger.dto;

public class NotificationsRequest {
   private boolean budgetAlerts;
   private boolean weeklyDigest;
   private boolean aiInsights;
   private boolean marketing;

   public boolean isBudgetAlerts() {
      return this.budgetAlerts;
   }

   public void setBudgetAlerts(boolean budgetAlerts) {
      this.budgetAlerts = budgetAlerts;
   }

   public boolean isWeeklyDigest() {
      return this.weeklyDigest;
   }

   public void setWeeklyDigest(boolean weeklyDigest) {
      this.weeklyDigest = weeklyDigest;
   }

   public boolean isAiInsights() {
      return this.aiInsights;
   }

   public void setAiInsights(boolean aiInsights) {
      this.aiInsights = aiInsights;
   }

   public boolean isMarketing() {
      return this.marketing;
   }

   public void setMarketing(boolean marketing) {
      this.marketing = marketing;
   }
}

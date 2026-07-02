package com.expense.ledger.dto;

public class PreferencesRequest {
   private String theme;
   private boolean reduceMotion;
   private boolean highContrast;
   private String currency;
   private String language;
   private String firstDayOfWeek;
   private String dateFormat;

   public String getTheme() {
      return this.theme;
   }

   public void setTheme(String theme) {
      this.theme = theme;
   }

   public boolean isReduceMotion() {
      return this.reduceMotion;
   }

   public void setReduceMotion(boolean reduceMotion) {
      this.reduceMotion = reduceMotion;
   }

   public boolean isHighContrast() {
      return this.highContrast;
   }

   public void setHighContrast(boolean highContrast) {
      this.highContrast = highContrast;
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

   public String getFirstDayOfWeek() {
      return this.firstDayOfWeek;
   }

   public void setFirstDayOfWeek(String firstDayOfWeek) {
      this.firstDayOfWeek = firstDayOfWeek;
   }

   public String getDateFormat() {
      return this.dateFormat;
   }

   public void setDateFormat(String dateFormat) {
      this.dateFormat = dateFormat;
   }
}

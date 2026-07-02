package com.expense.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordRequest {
   @NotBlank
   private String currentPassword;
   @NotBlank
   @Size(min = 8, message = "Password must be at least 8 characters")
   @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
      message = "Password must include uppercase, lowercase, number, and special character"
   )
   private String newPassword;
   @NotBlank
   private String confirmPassword;

   public String getCurrentPassword() {
      return this.currentPassword;
   }

   public void setCurrentPassword(String currentPassword) {
      this.currentPassword = currentPassword;
   }

   public String getNewPassword() {
      return this.newPassword;
   }

   public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
   }

   public String getConfirmPassword() {
      return this.confirmPassword;
   }

   public void setConfirmPassword(String confirmPassword) {
      this.confirmPassword = confirmPassword;
   }
}

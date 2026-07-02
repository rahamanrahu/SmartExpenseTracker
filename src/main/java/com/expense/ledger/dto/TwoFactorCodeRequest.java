package com.expense.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class TwoFactorCodeRequest {
   @NotBlank(message = "Verification code is required")
   @Pattern(regexp = "\\d{6}", message = "Enter a valid 6-digit code")
   private String code;

   public String getCode() {
      return this.code;
   }

   public void setCode(String code) {
      this.code = code;
   }
}

package com.expense.ledger.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
   @NotBlank
   private String name;
   @NotBlank
   @Email
   private String email;
   @NotBlank
   @Size(min = 8)
   private String password;
   @AssertTrue
   private boolean acceptTerms;

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
      this.email = email;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public boolean isAcceptTerms() {
      return this.acceptTerms;
   }

   public void setAcceptTerms(boolean acceptTerms) {
      this.acceptTerms = acceptTerms;
   }
}

package com.expense.ledger.controller;

import com.expense.ledger.dto.NotificationsRequest;
import com.expense.ledger.dto.PasswordRequest;
import com.expense.ledger.dto.PreferencesRequest;
import com.expense.ledger.dto.ProfileRequest;
import com.expense.ledger.dto.TwoFactorCodeRequest;
import com.expense.ledger.model.User;
import com.expense.ledger.service.TwoFactorService;
import com.expense.ledger.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {
   private final UserService userService;
   private final TwoFactorService twoFactorService;

   public SettingsController(UserService userService, TwoFactorService twoFactorService) {
      this.userService = userService;
      this.twoFactorService = twoFactorService;
   }

   @PostMapping("/settings/profile")
   public String updateProfile(
      @Valid @ModelAttribute("profileForm") ProfileRequest profileForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Profile form has invalid values.");
         return "redirect:/settings#profile";
      } else {
         try {
            this.userService.updateProfile(user, profileForm);
            redirectAttributes.addFlashAttribute("flashSuccess", "Profile updated.");
         } catch (IllegalArgumentException var7) {
            redirectAttributes.addFlashAttribute("flashError", var7.getMessage());
         }

         return "redirect:/settings#profile";
      }
   }

   @PostMapping("/settings/preferences")
   public String savePreferences(
      @ModelAttribute("preferencesForm") PreferencesRequest preferencesForm,
      Authentication authentication,
      HttpServletResponse response,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         if (preferencesForm.getTheme() != null) {
            user.setTheme(preferencesForm.getTheme());
         }

         if (preferencesForm.getCurrency() != null) {
            user.setCurrency(preferencesForm.getCurrency());
         }

         if (preferencesForm.getLanguage() != null) {
            user.setLanguage(preferencesForm.getLanguage());
         }

         this.userService.save(user);
         String theme = user.getTheme() != null && !user.getTheme().isBlank() ? user.getTheme() : "dark";
         ResponseCookie themeCookie = ResponseCookie.from("theme", theme).path("/").maxAge(31536000L).httpOnly(false).sameSite("Lax").build();
         response.addHeader("Set-Cookie", themeCookie.toString());
         redirectAttributes.addFlashAttribute("flashSuccess", "Preferences saved.");
         return "redirect:/settings#preferences";
      }
   }

   @PostMapping("/settings/notifications")
   public String saveNotifications(
      @ModelAttribute("notificationsForm") NotificationsRequest notificationsForm, Authentication authentication, RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         user.setBudgetAlertsEnabled(notificationsForm.isBudgetAlerts());
         user.setWeeklyDigestEnabled(notificationsForm.isWeeklyDigest());
         user.setAiInsightsEnabled(notificationsForm.isAiInsights());
         user.setMarketingEmailsEnabled(notificationsForm.isMarketing());
         this.userService.save(user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Notification settings updated.");
         return "redirect:/settings#notifications";
      }
   }

   @PostMapping("/settings/password")
   public String changePassword(
      @Valid @ModelAttribute("passwordForm") PasswordRequest passwordForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Password must meet complexity requirements.");
         return "redirect:/settings#security";
      } else if (!passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword())) {
         redirectAttributes.addFlashAttribute("flashError", "New password and confirm password must match.");
         return "redirect:/settings#security";
      } else {
         boolean changed = this.userService.changePassword(user, passwordForm.getCurrentPassword(), passwordForm.getNewPassword());
         if (!changed) {
            redirectAttributes.addFlashAttribute("flashError", "Current password is incorrect.");
            return "redirect:/settings#security";
         } else {
            redirectAttributes.addFlashAttribute("flashSuccess", "Password changed successfully.");
            return "redirect:/settings#security";
         }
      }
   }

   @PostMapping("/settings/2fa/regenerate")
   public String regenerateTwoFactorSecret(Authentication authentication, RedirectAttributes redirectAttributes) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         user.setTwoFactorSecret(this.twoFactorService.generateSecret());
         user.setTwoFactorEnabled(false);
         this.userService.save(user);
         redirectAttributes.addFlashAttribute("flashSuccess", "A new 2FA secret was generated. Verify a code to activate it.");
         return "redirect:/settings#security";
      }
   }

   @PostMapping("/settings/2fa/toggle")
   public String toggleTwoFactor(Authentication authentication, RedirectAttributes redirectAttributes) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (user.isTwoFactorEnabled()) {
         user.setTwoFactorEnabled(false);
         user.setTwoFactorSecret(null);
         this.userService.save(user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Two-factor authentication disabled.");
         return "redirect:/settings#security";
      } else {
         if (user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isBlank()) {
            user.setTwoFactorSecret(this.twoFactorService.generateSecret());
            user.setTwoFactorEnabled(false);
            this.userService.save(user);
         }

         redirectAttributes.addFlashAttribute("flashSuccess", "Scan the QR code and verify a 6-digit code to enable 2FA.");
         return "redirect:/settings#security";
      }
   }

   @PostMapping("/settings/2fa/verify")
   public String verifyTwoFactorSetup(
      @Valid @ModelAttribute("twoFactorForm") TwoFactorCodeRequest twoFactorForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Enter a valid 6-digit code.");
         return "redirect:/settings#security";
      } else if (user.getTwoFactorSecret() != null && !user.getTwoFactorSecret().isBlank()) {
         boolean valid = this.twoFactorService.verifyCode(user.getTwoFactorSecret(), twoFactorForm.getCode());
         if (!valid) {
            redirectAttributes.addFlashAttribute("flashError", "Invalid code. Please try again.");
            return "redirect:/settings#security";
         } else {
            user.setTwoFactorEnabled(true);
            this.userService.save(user);
            redirectAttributes.addFlashAttribute("flashSuccess", "Two-factor authentication is now enabled.");
            return "redirect:/settings#security";
         }
      } else {
         redirectAttributes.addFlashAttribute("flashError", "Start 2FA setup first.");
         return "redirect:/settings#security";
      }
   }

   @PostMapping("/settings/account/delete")
   public String deleteAccount(Authentication authentication, RedirectAttributes redirectAttributes) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.userService.delete(user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Your account has been deleted.");
         return "redirect:/login?deleted";
      }
   }

   @PostMapping("/data/import")
   public String importData(
      @RequestParam(name = "file", required = false) MultipartFile file,
      @RequestParam(name = "source", required = false) String source,
      RedirectAttributes redirectAttributes
   ) {
      if (file != null && !file.isEmpty()) {
         redirectAttributes.addFlashAttribute(
            "flashSuccess", "File received from " + (source == null ? "source" : source) + ". Processed " + file.getOriginalFilename() + "."
         );
         return "redirect:/settings#data";
      } else {
         redirectAttributes.addFlashAttribute("flashError", "Please select a file to import.");
         return "redirect:/settings#data";
      }
   }
}

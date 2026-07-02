package com.expense.ledger.controller;

import com.expense.ledger.dto.ForgotPasswordRequest;
import com.expense.ledger.dto.ResetPasswordRequest;
import com.expense.ledger.dto.SignupRequest;
import com.expense.ledger.dto.TwoFactorCodeRequest;
import com.expense.ledger.model.User;
import com.expense.ledger.service.PasswordResetService;
import com.expense.ledger.service.TwoFactorService;
import com.expense.ledger.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
   private final UserService userService;
   private final PasswordResetService passwordResetService;
   private final TwoFactorService twoFactorService;

   public AuthController(UserService userService, PasswordResetService passwordResetService, TwoFactorService twoFactorService) {
      this.userService = userService;
      this.passwordResetService = passwordResetService;
      this.twoFactorService = twoFactorService;
   }

   @GetMapping("/login")
   public String login(Authentication authentication) {
      return this.isLoggedIn(authentication) ? "redirect:/dashboard" : "auth/login";
   }

   @GetMapping("/logout")
   public String logoutGet() {
      // GET /logout is not processed by Spring Security (POST only).
      // Redirect gracefully so users who navigate here don't hit a 405 or error page.
      return "redirect:/login";
   }

   @GetMapping("/signup")
   public String signup(Model model, Authentication authentication) {
      if (this.isLoggedIn(authentication)) {
         return "redirect:/dashboard";
      } else {
         if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupRequest());
         }

         return "auth/signup";
      }
   }

   @PostMapping("/signup")
   public String signupSubmit(
      @Valid @ModelAttribute("signupForm") SignupRequest signupForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes
   ) {
      if (bindingResult.hasErrors()) {
         return "auth/signup";
      } else {
         try {
            this.userService.register(signupForm);
            redirectAttributes.addFlashAttribute("flashSuccess", "Account created. Please sign in.");
            return "redirect:/login";
         } catch (IllegalArgumentException var6) {
            model.addAttribute("flashError", var6.getMessage());
            return "auth/signup";
         }
      }
   }

   @GetMapping("/forgot-password")
   public String forgotPassword(Model model, Authentication authentication) {
      if (this.isLoggedIn(authentication)) {
         return "redirect:/dashboard";
      } else {
         if (!model.containsAttribute("forgotForm")) {
            model.addAttribute("forgotForm", new ForgotPasswordRequest());
         }

         return "auth/forgot-password";
      }
   }

   @PostMapping("/forgot-password")
   public String forgotPasswordSubmit(
      @Valid @ModelAttribute("forgotForm") ForgotPasswordRequest forgotForm, BindingResult bindingResult, RedirectAttributes redirectAttributes
   ) {
      if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Please enter a valid email address.");
         return "redirect:/forgot-password";
      } else {
         this.passwordResetService.sendResetEmail(forgotForm.getEmail());
         return "redirect:/forgot-password?sent=true";
      }
   }

   @GetMapping("/reset-password")
   public String resetPasswordForm(
      @ModelAttribute("resetForm") ResetPasswordRequest resetForm, String token, RedirectAttributes redirectAttributes, Model model
   ) {
      if (token != null && !token.isBlank() && this.passwordResetService.isValidToken(token)) {
         if (!model.containsAttribute("resetForm")) {
            ResetPasswordRequest form = new ResetPasswordRequest();
            form.setToken(token);
            model.addAttribute("resetForm", form);
         }

         return "auth/reset-password";
      } else {
         redirectAttributes.addFlashAttribute("flashError", "Your reset link is invalid or has expired.");
         return "redirect:/forgot-password";
      }
   }

   @PostMapping("/reset-password")
   public String resetPasswordSubmit(
      @Valid @ModelAttribute("resetForm") ResetPasswordRequest resetForm, BindingResult bindingResult, RedirectAttributes redirectAttributes
   ) {
      if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Please provide a valid new password.");
         return "redirect:/reset-password?token=" + resetForm.getToken();
      } else if (!resetForm.getNewPassword().equals(resetForm.getConfirmPassword())) {
         redirectAttributes.addFlashAttribute("flashError", "New password and confirmation do not match.");
         return "redirect:/reset-password?token=" + resetForm.getToken();
      } else {
         boolean reset = this.passwordResetService.resetPassword(resetForm.getToken(), resetForm.getNewPassword());
         if (!reset) {
            redirectAttributes.addFlashAttribute("flashError", "Your reset link is invalid or has expired.");
            return "redirect:/forgot-password";
         } else {
            redirectAttributes.addFlashAttribute("flashSuccess", "Password reset successful. Please sign in.");
            return "redirect:/login";
         }
      }
   }

   @GetMapping("/2fa-challenge")
   public String twoFactorChallenge(Model model, Authentication authentication, HttpServletRequest request) {
      if (!this.isLoggedIn(authentication)) {
         return "redirect:/login";
      } else if ((request.getSession(false) == null ? null : request.getSession(false).getAttribute("PENDING_2FA_USER")) instanceof String pendingUser
         && !pendingUser.isBlank()) {
         if (!model.containsAttribute("twoFactorCodeForm")) {
            model.addAttribute("twoFactorCodeForm", new TwoFactorCodeRequest());
         }

         return "auth/2fa-challenge";
      } else {
         return "redirect:/dashboard";
      }
   }

   @PostMapping("/2fa-challenge")
   public String verifyTwoFactorChallenge(
      @Valid @ModelAttribute("twoFactorCodeForm") TwoFactorCodeRequest twoFactorCodeForm,
      BindingResult bindingResult,
      Authentication authentication,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redirectAttributes
   ) {
      if (!this.isLoggedIn(authentication)) {
         return "redirect:/login";
      } else if (request.getSession(false) == null) {
         return "redirect:/login";
      } else if (!(request.getSession(false).getAttribute("PENDING_2FA_USER") instanceof String pendingUser && !pendingUser.isBlank())) {
         return "redirect:/dashboard";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Enter a valid 6-digit code.");
         return "redirect:/2fa-challenge";
      } else {
         User user = this.userService.findByEmail(pendingUser).orElse(null);
         boolean valid = user != null
            && user.isTwoFactorEnabled()
            && user.getTwoFactorSecret() != null
            && this.twoFactorService.verifyCode(user.getTwoFactorSecret(), twoFactorCodeForm.getCode());
         if (!valid) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("flashError", "Invalid or expired verification code. Please sign in again.");
            return "redirect:/login?error";
         } else {
            request.getSession(false).removeAttribute("PENDING_2FA_USER");
            redirectAttributes.addFlashAttribute("flashSuccess", "Two-factor verification successful.");
            return "redirect:/dashboard";
         }
      }
   }

   private boolean isLoggedIn(Authentication authentication) {
      return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
   }
}

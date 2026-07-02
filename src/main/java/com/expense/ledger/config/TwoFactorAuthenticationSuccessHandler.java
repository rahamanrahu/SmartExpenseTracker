package com.expense.ledger.config;

import com.expense.ledger.model.User;
import com.expense.ledger.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
   private final UserService userService;

   public TwoFactorAuthenticationSuccessHandler(UserService userService) {
      this.userService = userService;
   }

   public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
      User user = this.userService.findByEmail(authentication.getName()).orElse(null);
      if (user != null && user.isTwoFactorEnabled() && user.getTwoFactorSecret() != null && !user.getTwoFactorSecret().isBlank()) {
         request.getSession(true).setAttribute("PENDING_2FA_USER", user.getEmail());
         response.sendRedirect(request.getContextPath() + "/2fa-challenge");
      } else {
         if (request.getSession(false) != null) {
            request.getSession(false).removeAttribute("PENDING_2FA_USER");
         }

         response.sendRedirect(request.getContextPath() + "/dashboard");
      }
   }
}

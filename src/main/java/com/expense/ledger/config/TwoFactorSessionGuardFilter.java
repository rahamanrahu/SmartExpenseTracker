package com.expense.ledger.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TwoFactorSessionGuardFilter extends OncePerRequestFilter {
   public static final String PENDING_2FA_USER = "PENDING_2FA_USER";

   protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
      HttpSession session = request.getSession(false);
      if (session == null) {
         filterChain.doFilter(request, response);
      } else if (session.getAttribute("PENDING_2FA_USER") instanceof String pendingUser && !pendingUser.isBlank()) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            session.removeAttribute("PENDING_2FA_USER");
            filterChain.doFilter(request, response);
         } else if (!pendingUser.equalsIgnoreCase(authentication.getName())) {
            session.removeAttribute("PENDING_2FA_USER");
            filterChain.doFilter(request, response);
         } else {
            String path = request.getRequestURI().substring(request.getContextPath().length());
            if (this.isAllowedDuringChallenge(path)) {
               filterChain.doFilter(request, response);
            } else {
               response.sendRedirect(request.getContextPath() + "/2fa-challenge");
            }
         }
      } else {
         filterChain.doFilter(request, response);
      }
   }

   private boolean isAllowedDuringChallenge(String path) {
      return path.equals("/2fa-challenge")
         || path.equals("/logout")
         || path.startsWith("/css/")
         || path.startsWith("/js/")
         || path.startsWith("/images/")
         || path.startsWith("/webjars/")
         || path.equals("/favicon.ico")
         || path.startsWith("/error");
   }
}

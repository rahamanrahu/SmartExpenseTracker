package com.expense.ledger.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {
   private static final int MAX_ATTEMPTS = 10;
   private static final Duration WINDOW = Duration.ofMinutes(15L);
   private final Map<String, Deque<Instant>> attemptsByKey = new ConcurrentHashMap<>();

   protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
      if (!"POST".equalsIgnoreCase(request.getMethod())) {
         filterChain.doFilter(request, response);
      } else {
         String path = request.getRequestURI().substring(request.getContextPath().length());
         if (!"/login".equals(path) && !"/signup".equals(path)) {
            filterChain.doFilter(request, response);
         } else {
            String key = this.clientIp(request) + ":" + path;
            Deque<Instant> attempts = this.attemptsByKey.computeIfAbsent(key, k -> new ArrayDeque<>());
            boolean blocked;
            synchronized (attempts) {
               Instant cutoff = Instant.now().minus(WINDOW);

               while (!attempts.isEmpty() && attempts.peekFirst().isBefore(cutoff)) {
                  attempts.removeFirst();
               }

               blocked = attempts.size() >= MAX_ATTEMPTS;
               if (!blocked) {
                  attempts.addLast(Instant.now());
               }
            }

            if (blocked) {
               response.setHeader("Retry-After", String.valueOf(WINDOW.toSeconds()));
               String redirectPath = "/signup".equals(path)
                  ? request.getContextPath() + "/signup?locked=true"
                  : request.getContextPath() + "/login?locked=true";
               response.sendRedirect(redirectPath);
            } else {
               filterChain.doFilter(request, response);
            }
         }
      }
   }

   private String clientIp(HttpServletRequest request) {
      String forwardedFor = request.getHeader("X-Forwarded-For");
      return forwardedFor != null && !forwardedFor.isBlank() ? forwardedFor.split(",")[0].trim() : request.getRemoteAddr();
   }
}

package com.expense.ledger.service;

import com.expense.ledger.model.PasswordResetToken;
import com.expense.ledger.model.User;
import com.expense.ledger.repository.PasswordResetTokenRepository;
import com.expense.ledger.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
   private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
   private final PasswordResetTokenRepository tokenRepository;
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final JavaMailSender mailSender;
   @Value("${ledger.app.base-url:http://localhost:8080}")
   private String appBaseUrl;
   @Value("${spring.mail.username:no-reply@ledger.local}")
   private String mailFrom;

   public PasswordResetService(
      PasswordResetTokenRepository tokenRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender
   ) {
      this.tokenRepository = tokenRepository;
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
      this.mailSender = mailSender;
   }

   public void sendResetEmail(String email) {
      this.cleanupExpired();
      if (email != null && !email.isBlank()) {
         String normalized = email.trim().toLowerCase(Locale.ROOT);
         this.userRepository.findByEmail(normalized).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            this.tokenRepository.deleteByUser(user);
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(token);
            resetToken.setExpiresAt(LocalDateTime.now().plusHours(1L));
            this.tokenRepository.save(resetToken);
            this.sendEmail(user.getEmail(), token);
         });
      }
   }

   public boolean isValidToken(String token) {
      this.cleanupExpired();
      return this.tokenRepository
         .findByToken(token)
         .filter(t -> t.getExpiresAt() != null)
         .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
         .isPresent();
   }

   public boolean resetPassword(String token, String newPassword) {
      this.cleanupExpired();
      return this.tokenRepository
         .findByToken(token)
         .filter(t -> t.getExpiresAt() != null)
         .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
         .map(t -> {
            User user = t.getUser();
            user.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(user);
            this.tokenRepository.delete(t);
            return true;
         })
         .orElse(false);
   }

   private void cleanupExpired() {
      this.tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
   }

   private void sendEmail(String toEmail, String token) {
      String resetLink = this.appBaseUrl + "/reset-password?token=" + token;
      String body = "We received a request to reset your Ledger password.\n\nReset your password using this link (valid for 1 hour):\n"
         + resetLink
         + "\n\nIf you did not request this, you can safely ignore this email.";

      try {
         SimpleMailMessage msg = new SimpleMailMessage();
         msg.setFrom(this.mailFrom);
         msg.setTo(toEmail);
         msg.setSubject("Reset your Ledger password");
         msg.setText(body);
         this.mailSender.send(msg);
      } catch (Exception var6) {
         log.warn("Could not send reset email via SMTP. Reset link for {}: {}", new Object[]{toEmail, resetLink, var6});
         if (this.mailSender instanceof JavaMailSenderImpl) {
            log.info("Mail sender host configured as: {}", ((JavaMailSenderImpl)this.mailSender).getHost());
         }
      }
   }
}

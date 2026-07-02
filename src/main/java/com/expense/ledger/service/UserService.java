package com.expense.ledger.service;

import com.expense.ledger.dto.ProfileRequest;
import com.expense.ledger.dto.SignupRequest;
import com.expense.ledger.model.User;
import com.expense.ledger.repository.UserRepository;
import java.util.Locale;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;

   public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
   }

   @Transactional
   @CacheEvict(value = "users", key = "#result.email", condition = "#result != null")
   public User register(SignupRequest request) {
      String email = this.normalizeEmail(request.getEmail());
      if (this.userRepository.existsByEmail(email)) {
         throw new IllegalArgumentException("Email is already in use.");
      } else {
         User user = new User();
         user.setName(request.getName().trim());
         user.setEmail(email);
         user.setPassword(this.passwordEncoder.encode(request.getPassword()));
         return (User)this.userRepository.save(user);
      }
   }

   @Cacheable(value = "users", key = "#email != null ? #email.trim().toLowerCase() : ''", unless = "#result == null")
   public Optional<User> findByEmail(String email) {
      return email != null && !email.isBlank() ? this.userRepository.findByEmail(this.normalizeEmail(email)) : Optional.empty();
   }

   public User getCurrentUser(Authentication authentication) {
      return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)
         ? this.findByEmail(authentication.getName()).orElse(null)
         : null;
   }

   @Transactional
   @CacheEvict(value = "users", allEntries = true)
   public User updateProfile(User user, ProfileRequest request) {
      String newEmail = this.normalizeEmail(request.getEmail());
      Optional<User> existing = this.userRepository.findByEmail(newEmail);
      if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
         throw new IllegalArgumentException("Email is already in use.");
      } else {
         user.setName(request.getDisplayName().trim());
         user.setEmail(newEmail);
         if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
         }

         if (request.getTimezone() != null) {
            user.setTimezone(request.getTimezone().trim());
         }

         return (User)this.userRepository.save(user);
      }
   }

   @Transactional
   @CacheEvict(value = "users", key = "#user.email", condition = "#user != null && #user.email != null")
   public User save(User user) {
      if (user == null) {
         throw new IllegalArgumentException("User is required.");
      } else {
         return (User)this.userRepository.save(user);
      }
   }

   @Transactional
   @CacheEvict(value = "users", key = "#user.email")
   public void toggleTwoFactor(User user) {
      user.setTwoFactorEnabled(!user.isTwoFactorEnabled());
      this.userRepository.save(user);
   }

   @Transactional
   @CacheEvict(value = "users", key = "#user.email")
   public boolean changePassword(User user, String currentPassword, String newPassword) {
      if (!this.passwordEncoder.matches(currentPassword, user.getPassword())) {
         return false;
      } else {
         user.setPassword(this.passwordEncoder.encode(newPassword));
         this.userRepository.save(user);
         return true;
      }
   }

   @Transactional
   @CacheEvict(value = "users", key = "#user.email", condition = "#user != null")
   public void delete(User user) {
      if (user != null) {
         this.userRepository.delete(user);
      }
   }

   private String normalizeEmail(String email) {
      return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
   }
}

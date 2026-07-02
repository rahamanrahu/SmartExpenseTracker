package com.expense.ledger.repository;

import com.expense.ledger.model.PasswordResetToken;
import com.expense.ledger.model.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
   Optional<PasswordResetToken> findByToken(String token);

   void deleteByExpiresAtBefore(LocalDateTime cutoff);

   void deleteByUser(User user);
}

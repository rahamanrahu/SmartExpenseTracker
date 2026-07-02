package com.expense.ledger.repository;

import com.expense.ledger.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);

   boolean existsByEmail(String email);
}

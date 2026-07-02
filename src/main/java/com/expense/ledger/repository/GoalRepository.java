package com.expense.ledger.repository;

import com.expense.ledger.model.Goal;
import com.expense.ledger.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
   List<Goal> findByUserOrderByIdDesc(User user);

   Optional<Goal> findByIdAndUser(Long id, User user);
}

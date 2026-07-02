package com.expense.ledger.repository;

import com.expense.ledger.model.Budget;
import com.expense.ledger.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
   List<Budget> findByUserOrderByIdDesc(User user);

   Optional<Budget> findByUserAndCategoryIgnoreCase(User user, String category);
}

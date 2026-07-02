package com.expense.ledger.repository;

import com.expense.ledger.model.Expense;
import com.expense.ledger.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
   List<Expense> findByUserOrderByDateDesc(User user);

   Optional<Expense> findByIdAndUser(Long id, User user);

   List<Expense> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate start, LocalDate end);

   List<Expense> findByUserAndCategoryIgnoreCaseOrderByDateDesc(User user, String category);

   long countByUser(User user);
}

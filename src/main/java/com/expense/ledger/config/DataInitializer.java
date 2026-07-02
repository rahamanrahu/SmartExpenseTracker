package com.expense.ledger.config;

import com.expense.ledger.model.Budget;
import com.expense.ledger.model.Expense;
import com.expense.ledger.model.Goal;
import com.expense.ledger.model.User;
import com.expense.ledger.repository.BudgetRepository;
import com.expense.ledger.repository.ExpenseRepository;
import com.expense.ledger.repository.GoalRepository;
import com.expense.ledger.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
   private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

   @Bean
   CommandLineRunner seedDemoData(
      UserRepository userRepository,
      ExpenseRepository expenseRepository,
      BudgetRepository budgetRepository,
      GoalRepository goalRepository,
      PasswordEncoder passwordEncoder
   ) {
      return args -> {
         try {
            User demoUser = this.ensureUser(userRepository, passwordEncoder, "Demo User", "demo@ledger.app", "password");
            User alice = this.ensureUser(userRepository, passwordEncoder, "Alice Kumar", "alice@ledger.app", "password");
            User bob = this.ensureUser(userRepository, passwordEncoder, "Bob Singh", "bob@ledger.app", "password");
            this.seedExpenses(expenseRepository, demoUser, alice, bob);
            this.seedBudgets(budgetRepository, demoUser);
            this.seedGoals(goalRepository, demoUser);
            log.info("Demo seed complete. Users={}, demoExpenses={}", userRepository.count(), expenseRepository.findByUserOrderByDateDesc(demoUser).size());
         } catch (Exception var10) {
            log.error("Seed initialization failed but app will continue: {}", var10.getMessage(), var10);
         }
      };
   }

   private User ensureUser(UserRepository userRepository, PasswordEncoder passwordEncoder, String name, String email, String rawPassword) {
      return userRepository.findByEmail(email).orElseGet(() -> {
         User created = new User();
         created.setName(name);
         created.setEmail(email);
         created.setPassword(passwordEncoder.encode(rawPassword));
         return (User)userRepository.save(created);
      });
   }

   private void seedExpenses(ExpenseRepository expenseRepository, User demoUser, User alice, User bob) {
      if (expenseRepository.findByUserOrderByDateDesc(demoUser).isEmpty()) {
         List<Expense> expenses = new ArrayList<>();
         expenses.add(this.sampleExpense(demoUser, "Swiggy", "food", "Dinner", new BigDecimal("420"), LocalDate.now().minusDays(1L)));
         expenses.add(this.sampleExpense(demoUser, "Uber", "transport", "Office ride", new BigDecimal("210"), LocalDate.now().minusDays(2L)));
         expenses.add(this.sampleExpense(demoUser, "Amazon", "shopping", "Headphones", new BigDecimal("1499"), LocalDate.now().minusDays(3L)));
         expenses.add(this.sampleExpense(demoUser, "Airtel", "bills", "Mobile recharge", new BigDecimal("799"), LocalDate.now().minusDays(4L)));
         expenses.add(this.sampleExpense(demoUser, "Zomato", "food", "Weekend brunch", new BigDecimal("650"), LocalDate.now().minusDays(5L)));
         expenses.add(this.sampleExpense(demoUser, "Netflix", "entertainment", "Monthly plan", new BigDecimal("499"), LocalDate.now().minusDays(6L)));
         expenses.add(this.sampleExpense(alice, "BigBasket", "food", "Groceries", new BigDecimal("1250"), LocalDate.now().minusDays(2L)));
         expenses.add(this.sampleExpense(alice, "Metro", "transport", "Travel card", new BigDecimal("300"), LocalDate.now().minusDays(7L)));
         expenses.add(this.sampleExpense(bob, "PharmEasy", "health", "Medicines", new BigDecimal("780"), LocalDate.now().minusDays(3L)));
         expenses.add(this.sampleExpense(bob, "Flipkart", "shopping", "Keyboard", new BigDecimal("999"), LocalDate.now().minusDays(8L)));
         expenseRepository.saveAll(expenses);
      }
   }

   private void seedBudgets(BudgetRepository budgetRepository, User user) {
      if (budgetRepository.findByUserOrderByIdDesc(user).isEmpty()) {
         Budget foodBudget = this.sampleBudget(user, "food", new BigDecimal("9000"));
         Budget transportBudget = this.sampleBudget(user, "transport", new BigDecimal("5000"));
         Budget shoppingBudget = this.sampleBudget(user, "shopping", new BigDecimal("7000"));
         if (foodBudget != null) {
            budgetRepository.save(foodBudget);
         }

         if (transportBudget != null) {
            budgetRepository.save(transportBudget);
         }

         if (shoppingBudget != null) {
            budgetRepository.save(shoppingBudget);
         }
      }
   }

   private void seedGoals(GoalRepository goalRepository, User user) {
      if (goalRepository.findByUserOrderByIdDesc(user).isEmpty()) {
         Goal emergencyGoal = this.sampleGoal(user, "Emergency fund", new BigDecimal("100000"), new BigDecimal("25000"));
         Goal laptopGoal = this.sampleGoal(user, "New laptop", new BigDecimal("80000"), new BigDecimal("20000"));
         if (emergencyGoal != null) {
            goalRepository.save(emergencyGoal);
         }

         if (laptopGoal != null) {
            goalRepository.save(laptopGoal);
         }
      }
   }

   private Expense sampleExpense(User user, String merchant, String category, String note, BigDecimal amount, LocalDate date) {
      Expense expense = new Expense();
      expense.setUser(user);
      expense.setMerchant(merchant);
      expense.setCategory(category);
      expense.setNote(note);
      expense.setDate(date);
      expense.setAmount(amount.negate());
      expense.setTags("demo");
      return expense;
   }

   private Budget sampleBudget(User user, String category, BigDecimal limit) {
      Budget budget = new Budget();
      budget.setUser(user);
      budget.setCategory(category);
      budget.setLimitAmount(limit);
      return budget;
   }

   private Goal sampleGoal(User user, String name, BigDecimal target, BigDecimal current) {
      Goal goal = new Goal();
      goal.setUser(user);
      goal.setName(name);
      goal.setTargetAmount(target);
      goal.setCurrentAmount(current);
      return goal;
   }
}

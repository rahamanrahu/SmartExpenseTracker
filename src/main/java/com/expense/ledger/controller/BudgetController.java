package com.expense.ledger.controller;

import com.expense.ledger.dto.BudgetRequest;
import com.expense.ledger.dto.GoalRequest;
import com.expense.ledger.model.User;
import com.expense.ledger.service.BudgetService;
import com.expense.ledger.service.GoalService;
import com.expense.ledger.service.UserService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BudgetController {
   private final BudgetService budgetService;
   private final GoalService goalService;
   private final UserService userService;

   public BudgetController(BudgetService budgetService, GoalService goalService, UserService userService) {
      this.budgetService = budgetService;
      this.goalService = goalService;
      this.userService = userService;
   }

   @PostMapping("/budgets")
   public String createBudget(
      @Valid @ModelAttribute("budgetForm") BudgetRequest budgetForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Budget form has invalid values.");
         return "redirect:/budgets";
      } else {
         this.budgetService.createBudget(budgetForm, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Budget saved.");
         return "redirect:/budgets";
      }
   }

   @PostMapping("/budgets/{id}/delete")
   public String deleteBudget(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.budgetService.deleteBudget(id, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Budget deleted.");
         return "redirect:/budgets";
      }
   }

   @PostMapping("/budgets/{id}")
   public String updateBudget(
      @PathVariable Long id,
      @Valid @ModelAttribute("budgetForm") BudgetRequest budgetForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Budget form has invalid values.");
         return "redirect:/budgets";
      } else {
         this.budgetService.updateBudget(id, budgetForm, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Budget updated.");
         return "redirect:/budgets";
      }
   }

   @PostMapping("/goals")
   public String createGoal(
      @Valid @ModelAttribute("goalForm") GoalRequest goalForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Goal form has invalid values.");
         return "redirect:/budgets";
      } else {
         this.goalService.createGoal(goalForm, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Goal saved.");
         return "redirect:/budgets";
      }
   }

   @PostMapping("/goals/{id}/delete")
   public String deleteGoal(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.goalService.deleteGoal(id, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Goal deleted.");
         return "redirect:/budgets";
      }
   }

   @PostMapping("/goals/{id}")
   public String updateGoal(
      @PathVariable Long id,
      @Valid @ModelAttribute("goalForm") GoalRequest goalForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Goal form has invalid values.");
         return "redirect:/budgets";
      } else {
         this.goalService.updateGoal(id, goalForm, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Goal updated.");
         return "redirect:/budgets";
      }
   }

   @PostMapping("/goals/{id}/contribute")
   public String contributeToGoal(
      @PathVariable Long id,
      @RequestParam(name = "amount", required = false) BigDecimal amount,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
         boolean reached = this.goalService.contribute(id, amount, user);
         if (reached) {
            redirectAttributes.addFlashAttribute("flashSuccess", "\ud83c\udf89 Goal reached! Congratulations!");
         } else {
            redirectAttributes.addFlashAttribute("flashSuccess", "Funds added to your goal.");
         }

         return "redirect:/budgets";
      } else {
         redirectAttributes.addFlashAttribute("flashError", "Please enter a positive amount.");
         return "redirect:/budgets";
      }
   }
}

package com.expense.ledger.controller;

import com.expense.ledger.dto.ExpenseRequest;
import com.expense.ledger.model.User;
import com.expense.ledger.service.ExpenseService;
import com.expense.ledger.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExpenseController {
   private final ExpenseService expenseService;
   private final UserService userService;

   public ExpenseController(ExpenseService expenseService, UserService userService) {
      this.expenseService = expenseService;
      this.userService = userService;
   }

   @PostMapping("/expenses")
   public String addExpense(
      @Valid @ModelAttribute("expenseForm") ExpenseRequest expenseForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Please complete required expense fields.");
         return "redirect:/expenses";
      } else {
         this.expenseService.addExpense(expenseForm, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Expense saved.");
         return "redirect:/expenses";
      }
   }

   @GetMapping("/expenses/{id}/edit")
   public String editExpense(@PathVariable Long id) {
      return "redirect:/expenses?edit=" + id;
   }

   @PostMapping("/expenses/{id}")
   public String updateExpense(
      @PathVariable Long id,
      @Valid @ModelAttribute("expenseForm") ExpenseRequest expenseForm,
      BindingResult bindingResult,
      Authentication authentication,
      RedirectAttributes redirectAttributes
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else if (bindingResult.hasErrors()) {
         redirectAttributes.addFlashAttribute("flashError", "Please complete required expense fields.");
         return "redirect:/expenses?edit=" + id;
      } else {
         this.expenseService.updateExpense(id, expenseForm, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Expense updated.");
         return "redirect:/expenses";
      }
   }

   @PostMapping("/expenses/{id}/delete")
   public String deleteExpense(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.expenseService.deleteExpense(id, user);
         redirectAttributes.addFlashAttribute("flashSuccess", "Expense deleted.");
         return "redirect:/expenses";
      }
   }
}

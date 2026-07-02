package com.expense.ledger.controller;

import com.expense.ledger.model.Expense;
import com.expense.ledger.model.User;
import com.expense.ledger.service.ExpenseService;
import com.expense.ledger.service.UserService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExportController {
   private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
   private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
   private final UserService userService;
   private final ExpenseService expenseService;

   public ExportController(UserService userService, ExpenseService expenseService) {
      this.userService = userService;
      this.expenseService = expenseService;
   }

   // ---- Reports export ----

   @GetMapping(value = "/reports/export", params = "format=csv")
   public ResponseEntity<byte[]> exportReportsCsv(@RequestParam(name = "month", required = false) String month, Authentication authentication) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return ((BodyBuilder)ResponseEntity.status(302).header("Location", new String[]{"/login"})).build();
      } else {
         List<Expense> expenses = this.expenseService.getAllExpenses(user);
         YearMonth selected = this.resolveMonth(month);
         List<Expense> scoped = expenses.stream().filter(ex -> ex.getDate() != null).filter(ex -> YearMonth.from(ex.getDate()).equals(selected)).toList();
         String filename = "ledger-" + selected + ".csv";
         return ((BodyBuilder)ResponseEntity.ok()
               .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
               .header("Content-Disposition", new String[]{"attachment; filename=\"" + filename + "\""}))
            .body(toCsv(scoped).getBytes(StandardCharsets.UTF_8));
      }
   }

   @GetMapping(value = "/reports/export", params = "format=pdf")
   public String exportReportsPdfStub(@RequestParam(name = "month", required = false) String month, RedirectAttributes redirectAttributes) {
      redirectAttributes.addFlashAttribute("flashError", "PDF export is not enabled yet. Please use CSV export.");
      return month != null && !month.isBlank() ? "redirect:/reports?month=" + month : "redirect:/reports";
   }

   // ---- Full data export (from Settings → Data & Export panel) ----

   @GetMapping(value = "/data/export", params = "format=csv")
   public ResponseEntity<byte[]> exportAllCsv(Authentication authentication) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return ((BodyBuilder)ResponseEntity.status(302).header("Location", new String[]{"/login"})).build();
      }
      List<Expense> all = this.expenseService.getAllExpenses(user);
      return ((BodyBuilder)ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .header("Content-Disposition", new String[]{"attachment; filename=\"ledger-all-expenses.csv\""}))
         .body(toCsv(all).getBytes(StandardCharsets.UTF_8));
   }

   @GetMapping(value = "/data/export", params = "format=json")
   public ResponseEntity<byte[]> exportAllJson(Authentication authentication) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return ((BodyBuilder)ResponseEntity.status(302).header("Location", new String[]{"/login"})).build();
      }
      List<Expense> all = this.expenseService.getAllExpenses(user);
      String json = toJson(all);
      return ((BodyBuilder)ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
            .header("Content-Disposition", new String[]{"attachment; filename=\"ledger-all-expenses.json\""}))
         .body(json.getBytes(StandardCharsets.UTF_8));
   }

   @GetMapping(value = "/data/export", params = "format=pdf")
   public String exportAllPdfStub(RedirectAttributes redirectAttributes) {
      redirectAttributes.addFlashAttribute("flashError", "PDF export is not enabled yet. Please use CSV export.");
      return "redirect:/settings#data";
   }

   private YearMonth resolveMonth(String month) {
      if (month != null && !month.isBlank()) {
         try {
            return YearMonth.parse(month, MONTH_YEAR);
         } catch (DateTimeParseException var3) {
            return YearMonth.now();
         }
      } else {
         return YearMonth.now();
      }
   }

   private String toCsv(List<Expense> expenses) {
      StringBuilder csv = new StringBuilder();
      csv.append("Date,Merchant,Category,Amount,Notes,Tags\n");
      for (Expense e : expenses) {
         csv.append(csvField(e.getDate() == null ? "" : e.getDate().format(ISO_DATE))).append(',');
         csv.append(csvField(safe(e.getMerchant()))).append(',');
         csv.append(csvField(safe(e.getCategory()))).append(',');
         csv.append(csvField(formatAmount(e.getAmount()))).append(',');
         csv.append(csvField(safe(e.getNote()))).append(',');
         csv.append(csvField(safe(e.getTags()))).append('\n');
      }
      return csv.toString();
   }

   private String toJson(List<Expense> expenses) {
      StringBuilder sb = new StringBuilder("[\n");
      for (int i = 0; i < expenses.size(); i++) {
         Expense e = expenses.get(i);
         sb.append("  {");
         sb.append("\"date\":\"").append(e.getDate() == null ? "" : e.getDate().format(ISO_DATE)).append("\",");
         sb.append("\"merchant\":\"").append(jsonEsc(safe(e.getMerchant()))).append("\",");
         sb.append("\"category\":\"").append(jsonEsc(safe(e.getCategory()))).append("\",");
         sb.append("\"amount\":").append(formatAmount(e.getAmount())).append(",");
         sb.append("\"notes\":\"").append(jsonEsc(safe(e.getNote()))).append("\",");
         sb.append("\"tags\":\"").append(jsonEsc(safe(e.getTags()))).append("\"");
         sb.append("}").append(i < expenses.size() - 1 ? "," : "").append("\n");
      }
      sb.append("]");
      return sb.toString();
   }

   private String safe(String value) {
      return value == null ? "" : value;
   }

   private String formatAmount(BigDecimal amount) {
      return amount == null ? "0.00" : amount.abs().toPlainString();
   }

   private String csvField(String value) {
      String raw = value == null ? "" : value;
      String escaped = raw.replace("\"", "\"\"");
      return "\"" + escaped + "\"";
   }

   private String jsonEsc(String value) {
      if (value == null) return "";
      return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
   }
}

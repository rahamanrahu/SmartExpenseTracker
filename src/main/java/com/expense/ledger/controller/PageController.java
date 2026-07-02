package com.expense.ledger.controller;

import com.expense.ledger.dto.BudgetRequest;
import com.expense.ledger.dto.ExpenseRequest;
import com.expense.ledger.dto.GoalRequest;
import com.expense.ledger.dto.NotificationsRequest;
import com.expense.ledger.dto.PasswordRequest;
import com.expense.ledger.dto.PreferencesRequest;
import com.expense.ledger.dto.ProfileRequest;
import com.expense.ledger.dto.TwoFactorCodeRequest;
import com.expense.ledger.model.Budget;
import com.expense.ledger.model.Expense;
import com.expense.ledger.model.Goal;
import com.expense.ledger.model.User;
import com.expense.ledger.service.AiService;
import com.expense.ledger.service.BudgetService;
import com.expense.ledger.service.ExpenseService;
import com.expense.ledger.service.GoalService;
import com.expense.ledger.service.TwoFactorService;
import com.expense.ledger.service.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {
   private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
   private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
   private static final int PAGE_SIZE = 20;
   private final UserService userService;
   private final ExpenseService expenseService;
   private final BudgetService budgetService;
   private final GoalService goalService;
   private final AiService aiService;
   private final TwoFactorService twoFactorService;

   public PageController(
      UserService userService,
      ExpenseService expenseService,
      BudgetService budgetService,
      GoalService goalService,
      AiService aiService,
      TwoFactorService twoFactorService
   ) {
      this.userService = userService;
      this.expenseService = expenseService;
      this.budgetService = budgetService;
      this.goalService = goalService;
      this.aiService = aiService;
      this.twoFactorService = twoFactorService;
   }

   @GetMapping("/")
   public String index() {
      return "index";
   }

   @GetMapping("/dashboard")
   public String dashboard(Model model, Authentication authentication) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.addCommonModel(model, user);
         List<Expense> expenses = this.expenseService.getAllExpenses(user);
         List<Budget> budgets = this.budgetService.getBudgets(user);
         BigDecimal spent = this.expenseService.getSpentForPeriod(user, "monthly");
         BigDecimal budgetTotal = this.budgetService.totalBudget(user, "monthly");
         BigDecimal budgetLeft = budgetTotal.compareTo(BigDecimal.ZERO) > 0 ? budgetTotal.subtract(spent).max(BigDecimal.ZERO) : BigDecimal.ZERO;
         int usedPct = budgetTotal.compareTo(BigDecimal.ZERO) > 0 ? this.toPercent(spent, budgetTotal) : 0;
         Map<String, Object> stats = new HashMap<>();
         stats.put("spentFmt", this.formatMoney(spent));
         stats.put("budgetFmt", this.formatMoney(budgetTotal));
         stats.put("budgetUsedPct", usedPct);
         stats.put("budgetLeftPct", Math.max(0, 100 - usedPct));
         stats.put("budgetLeftFmt", this.formatMoney(budgetLeft));
         stats.put("hasBudget", budgetTotal.compareTo(BigDecimal.ZERO) > 0);
         model.addAttribute("stats", stats);
         model.addAttribute("greeting", this.greeting());
         model.addAttribute("periodLabel", "Here is how your money moved this month.");
         model.addAttribute("totalCount", expenses.size());
         model.addAttribute("recentTxns", this.expenseService.getRecentExpenses(user, 6).stream().map(this::toRecentTxnView).toList());
         model.addAttribute("budgetPreview", this.toBudgetPreviewViews(budgets, expenses));
         model.addAttribute("hasExpenses", !expenses.isEmpty());
         model.addAttribute("hasBudgets", !budgets.isEmpty());
         if (!expenses.isEmpty()) {
            Map<String, Object> aiInsight = new HashMap<>();
            aiInsight.put("headline", this.aiService.generateInsight(user, expenses, budgets, this.goalService.getGoals(user)));
            model.addAttribute("aiInsight", aiInsight);
         }
         model.addAttribute("aiProviderStatus", this.aiService.providerStatus());

         model.addAttribute("categoryChartData", this.buildCategoryChartData(expenses));
         model.addAttribute("trendChartData", this.buildTrendChartData(expenses));
         return "dashboard";
      }
   }

   @GetMapping("/expenses")
   public String expenses(
      @RequestParam(name = "q", required = false) String q,
      @RequestParam(name = "range", defaultValue = "all") String range,
      @RequestParam(name = "category", required = false) String category,
      @RequestParam(name = "min", required = false) BigDecimal min,
      @RequestParam(name = "max", required = false) BigDecimal max,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "edit", required = false) Long edit,
      Model model,
      Authentication authentication
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.addCommonModel(model, user);
         List<Expense> filtered = this.expenseService.getAllExpenses(user).stream().filter(e -> this.matchesFilter(e, q, category, min, max)).toList();
         int total = filtered.size();
         int totalPages = Math.max(1, (int)Math.ceil(total / (double)PAGE_SIZE));
         int currentPage = Math.max(0, Math.min(page, totalPages - 1));
         int fromIndex = Math.min(currentPage * PAGE_SIZE, total);
         int toIndex = Math.min(fromIndex + PAGE_SIZE, total);
         List<Expense> pageItems = filtered.subList(fromIndex, toIndex);
         int pageStart = Math.max(0, currentPage - 2);
         int pageEnd = Math.min(totalPages - 1, pageStart + 4);
         pageStart = Math.max(0, pageEnd - 4);
         List<Integer> pageNumbers = new ArrayList<>();

         for (int p = pageStart; p <= pageEnd; p++) {
            pageNumbers.add(p);
         }

         model.addAttribute("expenses", pageItems.stream().map(this::toExpenseRowView).toList());
         model.addAttribute("totalCount", total);
         model.addAttribute("totalSpentFmt", this.formatMoney(this.sumAbs(filtered)));
         model.addAttribute("hasExpenses", !filtered.isEmpty());
         Map<String, Object> filter = new HashMap<>();
         filter.put("q", q);
         filter.put("range", range);
         filter.put("category", category);
         filter.put("min", min);
         filter.put("max", max);
         model.addAttribute("filter", filter);
         Map<String, Object> pageInfo = new HashMap<>();
         pageInfo.put("from", total == 0 ? 0 : fromIndex + 1);
         pageInfo.put("to", toIndex);
         pageInfo.put("total", total);
         pageInfo.put("hasPrev", currentPage > 0);
         pageInfo.put("hasNext", currentPage < totalPages - 1);
         pageInfo.put("prev", Math.max(0, currentPage - 1));
         pageInfo.put("next", Math.min(totalPages - 1, currentPage + 1));
         pageInfo.put("current", currentPage);
         pageInfo.put("numbers", pageNumbers);
         model.addAttribute("page", pageInfo);
         ExpenseRequest req = edit != null
            ? this.expenseService.resolveExpense(edit, user).map(this::toExpenseRequest).orElse(this.defaultExpenseForm())
            : this.defaultExpenseForm();
         model.addAttribute("expenseForm", req);
         return "expenses";
      }
   }

   @GetMapping("/budgets")
   public String budgets(Model model, Authentication authentication) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.addCommonModel(model, user);
         List<Expense> expenses = this.expenseService.getAllExpenses(user);
         List<Budget> budgets = this.budgetService.getBudgets(user);
         List<Goal> goals = this.goalService.getGoals(user);
         BigDecimal spent = this.expenseService.getSpentForPeriod(user, "monthly");
         BigDecimal totalBudget = this.budgetService.totalBudget(user, "monthly");
         BigDecimal remaining = totalBudget.compareTo(BigDecimal.ZERO) > 0 ? totalBudget.subtract(spent).max(BigDecimal.ZERO) : BigDecimal.ZERO;
         int usedPct = totalBudget.compareTo(BigDecimal.ZERO) > 0 ? this.toPercent(spent, totalBudget) : 0;
         BigDecimal projected = this.dailyProjection(expenses, totalBudget);
         Map<String, Object> overall = new HashMap<>();
         overall.put("pct", usedPct);
         overall.put("totalFmt", this.formatMoney(totalBudget));
         overall.put("hasBudget", totalBudget.compareTo(BigDecimal.ZERO) > 0);
         overall.put(
            "periodLabel",
            LocalDate.now().getMonth().name().substring(0, 1)
               + LocalDate.now().getMonth().name().substring(1).toLowerCase(Locale.ROOT)
               + " "
               + LocalDate.now().getYear()
         );
         overall.put("sentimentWord", usedPct <= 70 ? "comfortably" : "carefully");
         overall.put(
            "narrative",
            totalBudget.compareTo(BigDecimal.ZERO) > 0
               ? "You have " + this.formatMoney(remaining) + " left for this period."
               : "Set up a budget to start tracking your spending."
         );
         overall.put("spentFmt", this.formatMoney(spent));
         overall.put("remainingFmt", this.formatMoney(remaining));
         overall.put("projectedFmt", this.formatMoney(projected));
         overall.put("ringOffset", 540 - Math.round(usedPct / 100.0F * 540.0F));
         model.addAttribute("overall", overall);
         model.addAttribute("budgets", this.toBudgetCardViews(budgets, expenses));
         model.addAttribute("goals", this.toGoalViews(goals));
         model.addAttribute("hasBudgets", !budgets.isEmpty());
         model.addAttribute("hasGoals", !goals.isEmpty());
         model.addAttribute("streak", Map.of("days", this.computeStreak(expenses)));
         BudgetRequest budgetForm = new BudgetRequest();
         budgetForm.setCategory("food");
         budgetForm.setAmount(BigDecimal.ZERO);
         budgetForm.setPeriod("monthly");
         model.addAttribute("budgetForm", budgetForm);
         GoalRequest goalForm = new GoalRequest();
         goalForm.setCurrentAmount(BigDecimal.ZERO);
         model.addAttribute("goalForm", goalForm);
         return "budgets";
      }
   }

   @GetMapping("/reports")
   public String reports(
      @RequestParam(name = "mode", defaultValue = "monthly") String mode,
      @RequestParam(name = "month", required = false) String month,
      Model model,
      Authentication authentication
   ) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.addCommonModel(model, user);
         List<Expense> expenses = this.expenseService.getAllExpenses(user);
         List<String> months = this.buildMonthList(expenses);
         String selectedMonth = month != null && !month.isBlank() ? month : (months.isEmpty() ? LocalDate.now().format(MONTH_YEAR) : months.get(0));
         List<Expense> selectedExpenses = this.filterExpensesByMonth(expenses, selectedMonth);
         BigDecimal spent = this.sumAbs(selectedExpenses);
         Map<String, Object> period = new HashMap<>();
         period.put("mode", mode);
         period.put("month", selectedMonth);
         period.put("label", "Monthly breakdown — " + selectedMonth);
         period.put("months", months);
         model.addAttribute("period", period);
         Map<String, Object> stats = new HashMap<>();
         stats.put("totalSpentFmt", this.formatMoney(spent));
         stats.put("spentDelta", "+0.0%");
         stats.put("incomeFmt", this.formatMoney(BigDecimal.ZERO));
         stats.put("incomeDelta", "+0.0%");
         stats.put("savingsFmt", this.formatMoney(BigDecimal.ZERO.subtract(spent)));
         stats.put("savingsPct", spent.compareTo(BigDecimal.ZERO) > 0 ? 0 : 100);
         stats.put("hasData", !selectedExpenses.isEmpty());
         model.addAttribute("stats", stats);
         model.addAttribute("topMerchants", this.toTopMerchantViews(selectedExpenses));
         model.addAttribute("hasExpenses", !selectedExpenses.isEmpty());
         model.addAttribute("categoryChartData", this.buildCategoryChartData(selectedExpenses));
         model.addAttribute("trendChartData", this.buildTrendChartData(selectedExpenses));
         return "reports";
      }
   }

   @GetMapping("/settings")
   public String settings(Model model, Authentication authentication) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         this.addCommonModel(model, user);
         ProfileRequest profileForm = new ProfileRequest();
         profileForm.setDisplayName(user.getName());
         profileForm.setEmail(user.getEmail());
         profileForm.setUsername(user.getEmail().contains("@") ? user.getEmail().split("@")[0] : user.getEmail());
         profileForm.setPhone(user.getPhone() != null ? user.getPhone() : "");
         profileForm.setTimezone(user.getTimezone() != null ? user.getTimezone() : "Asia/Kolkata");
         model.addAttribute("profileForm", profileForm);
         PreferencesRequest preferencesForm = new PreferencesRequest();
         preferencesForm.setTheme(user.getTheme() != null ? user.getTheme() : "dark");
         preferencesForm.setCurrency(user.getCurrency() != null ? user.getCurrency() : "INR");
         preferencesForm.setLanguage(user.getLanguage() != null ? user.getLanguage() : "en-IN");
         preferencesForm.setFirstDayOfWeek("MONDAY");
         preferencesForm.setDateFormat("dd/MM/yyyy");
         model.addAttribute("preferencesForm", preferencesForm);
         NotificationsRequest notificationsForm = new NotificationsRequest();
         notificationsForm.setBudgetAlerts(user.isBudgetAlertsEnabled());
         notificationsForm.setWeeklyDigest(user.isWeeklyDigestEnabled());
         notificationsForm.setAiInsights(user.isAiInsightsEnabled());
         notificationsForm.setMarketing(user.isMarketingEmailsEnabled());
         model.addAttribute("notificationsForm", notificationsForm);
         model.addAttribute("passwordForm", new PasswordRequest());
         model.addAttribute("twoFactorForm", new TwoFactorCodeRequest());
         boolean setupPending = !user.isTwoFactorEnabled() && user.getTwoFactorSecret() != null && !user.getTwoFactorSecret().isBlank();
         model.addAttribute("twoFactorSetupPending", setupPending);
         if (setupPending) {
            String otpAuthUri = this.twoFactorService.buildOtpAuthUri(user.getEmail(), user.getTwoFactorSecret());
            model.addAttribute("twoFactorQrCode", this.twoFactorService.buildQrCodeDataUri(otpAuthUri));
            model.addAttribute("twoFactorManualSecret", user.getTwoFactorSecret());
         }

         String planName = user.getPlanName() == null || user.getPlanName().isBlank() ? "Free" : user.getPlanName();
         model.addAttribute("plan", Map.of(
            "name", planName,
            "isPro", "Pro".equalsIgnoreCase(planName),
            "updatedAt", user.getPlanUpdatedAt() != null ? user.getPlanUpdatedAt().toString() : ""
         ));
         return "settings";
      }
   }

   @GetMapping("/expenses/export")
   public String exportExpenses() {
      return "redirect:/expenses";
   }

   @GetMapping("/reports/export")
   public String exportReports() {
      return "redirect:/reports";
   }

   @GetMapping("/data/export")
   public String exportData() {
      return "redirect:/settings";
   }

   @GetMapping("/privacy")
   public String privacy() {
      return "redirect:/";
   }

   @GetMapping("/terms")
   public String terms() {
      return "redirect:/";
   }

   @GetMapping("/billing/upgrade")
   public String billingUpgrade(Authentication authentication) {
      User user = this.userService.getCurrentUser(authentication);
      if (user == null) {
         return "redirect:/login";
      } else {
         user.setPlanName("Pro");
         user.setPlanUpdatedAt(LocalDateTime.now());
         this.userService.save(user);
         return "redirect:/settings#billing";
      }
   }

   private void addCommonModel(Model model, User user) {
      model.addAttribute("user", user);
      model.addAttribute("notifications", this.buildNotifications());
      model.addAttribute("categories", this.categoryOptions());
      model.addAttribute("accounts", this.accountOptions());
      if (!model.containsAttribute("expenseForm")) {
         model.addAttribute("expenseForm", this.defaultExpenseForm());
      }

      try {
         List<Expense> recent = this.expenseService.getAllExpenses(user);
         List<String> hints = recent.stream().map(Expense::getMerchant).filter(m -> m != null && !m.isBlank()).distinct().limit(10L).toList();
         model.addAttribute("merchantHints", hints);
      } catch (Exception var5) {
         model.addAttribute("merchantHints", Collections.emptyList());
      }
   }

   private List<Map<String, Object>> buildNotifications() {
      return Collections.emptyList();
   }

   private Map<String, Object> buildCategoryChartData(List<Expense> expenses) {
      Map<String, BigDecimal> byCategory = this.spentByCategory(expenses);
      List<String> labels = new ArrayList<>(byCategory.keySet());
      List<Double> data = labels.stream().map(l -> byCategory.get(l).doubleValue()).toList();
      return Map.of("labels", labels, "data", data, "hasData", !expenses.isEmpty());
   }

   private Map<String, Object> buildTrendChartData(List<Expense> expenses) {
      Map<String, BigDecimal> byMonth = new LinkedHashMap<>();
      LocalDate now = LocalDate.now();

      for (int i = 5; i >= 0; i--) {
         LocalDate m = now.minusMonths(i);
         byMonth.put(m.format(MONTH_YEAR), BigDecimal.ZERO);
      }

      for (Expense e : expenses) {
         if (e.getDate() != null) {
            String key = e.getDate().format(MONTH_YEAR);
            if (byMonth.containsKey(key)) {
               byMonth.merge(key, this.safeMoney(e.getAmount()).abs(), BigDecimal::add);
            }
         }
      }

      return Map.of(
         "labels", new ArrayList<>(byMonth.keySet()), "data", byMonth.values().stream().map(BigDecimal::doubleValue).toList(), "hasData", !expenses.isEmpty()
      );
   }

   private List<String> buildMonthList(List<Expense> expenses) {
      Set<String> set = new LinkedHashSet<>();
      set.add(LocalDate.now().format(MONTH_YEAR));
      expenses.stream().filter(e -> e.getDate() != null).map(e -> e.getDate().format(MONTH_YEAR)).forEach(set::add);
      return new ArrayList<>(set);
   }

   private int computeStreak(List<Expense> expenses) {
      if (expenses.isEmpty()) {
         return 0;
      } else {
         Set<LocalDate> dates = expenses.stream().filter(e -> e.getDate() != null).map(Expense::getDate).collect(Collectors.toSet());
         int streak = 0;

         for (LocalDate day = LocalDate.now(); dates.contains(day); day = day.minusDays(1L)) {
            streak++;
         }

         return streak;
      }
   }

   private BigDecimal dailyProjection(List<Expense> expenses, BigDecimal totalBudget) {
      if (expenses.isEmpty()) {
         return BigDecimal.ZERO;
      } else {
         int daysInMonth = LocalDate.now().lengthOfMonth();
         int dayOfMonth = LocalDate.now().getDayOfMonth();
         if (dayOfMonth == 0) {
            return BigDecimal.ZERO;
         } else {
            BigDecimal spent = this.sumAbs(expenses);
            BigDecimal dailyAvg = spent.divide(BigDecimal.valueOf((long)dayOfMonth), 2, RoundingMode.HALF_UP);
            BigDecimal projected = dailyAvg.multiply(BigDecimal.valueOf((long)daysInMonth));
            return projected.min(totalBudget.compareTo(BigDecimal.ZERO) > 0 ? totalBudget.multiply(new BigDecimal("2")) : projected);
         }
      }
   }

   private List<Map<String, Object>> toBudgetPreviewViews(List<Budget> budgets, List<Expense> expenses) {
      return budgets.stream().map(b -> {
         String cat = this.safeCategory(b.getCategory());
         BigDecimal s = this.spentForCategoryAndPeriod(expenses, cat, b.getPeriod());
         BigDecimal lim = this.safeMoney(b.getLimitAmount());
         int pct = this.toPercent(s, lim);
         Map<String, Object> v = new HashMap<>();
         v.put("label", this.categoryLabel(cat));
         v.put("spentFmt", this.formatMoney(s));
         v.put("budgetFmt", this.formatMoney(lim));
         v.put("pct", pct);
         v.put("level", pct >= 95 ? "danger" : (pct >= 80 ? "warn" : "ok"));
         return v;
      }).toList();
   }

   private List<Map<String, Object>> toBudgetCardViews(List<Budget> budgets, List<Expense> expenses) {
      return budgets.stream().map(b -> {
         String cat = this.safeCategory(b.getCategory());
         BigDecimal s = this.spentForCategoryAndPeriod(expenses, cat, b.getPeriod());
         BigDecimal lim = this.safeMoney(b.getLimitAmount());
         BigDecimal rem = lim.subtract(s).max(BigDecimal.ZERO);
         int pct = this.toPercent(s, lim);
         Map<String, Object> v = new HashMap<>();
         v.put("id", b.getId());
         v.put("name", this.categoryLabel(cat));
         v.put("category", cat);
         v.put("emoji", this.categoryBgClass(cat));
         v.put("budgetFmt", this.formatMoney(lim));
         v.put("period", b.getPeriod() != null ? b.getPeriod() : "monthly");
         v.put("spentFmt", this.formatMoney(s));
         v.put("remainingFmt", this.formatMoney(rem));
         v.put("pct", pct);
         v.put("level", pct >= 95 ? "danger" : (pct >= 80 ? "warn" : "ok"));
         return v;
      }).toList();
   }

   private List<Map<String, Object>> toGoalViews(List<Goal> goals) {
      String[] colors = new String[]{"#7c5cff", "#34d399", "#fbbf24", "#60a5fa", "#f472b6", "#f87171"};
      List<Map<String, Object>> views = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
         Goal g = goals.get(i);
         BigDecimal target = this.safeMoney(g.getTargetAmount());
         BigDecimal current = this.safeMoney(g.getCurrentAmount());
         Map<String, Object> v = new HashMap<>();
         v.put("id", g.getId());
         v.put("name", g.getName());
         v.put("deadline", g.getDeadline() != null ? g.getDeadline().format(MONTH_YEAR) : "No deadline");
         v.put("color", colors[i % colors.length]);
         v.put("emoji", g.getEmoji() != null && !g.getEmoji().isBlank() ? g.getEmoji() : "\ud83c\udfaf");
         v.put("pct", this.toPercent(current, target));
         v.put("savedFmt", this.formatMoney(current));
         v.put("targetFmt", this.formatMoney(target));
         views.add(v);
      }

      return views;
   }

   private List<Map<String, Object>> toTopMerchantViews(List<Expense> expenses) {
      Map<String, BigDecimal> totals = new HashMap<>();
      Map<String, String> catByMerchant = new HashMap<>();
      Map<String, Integer> counts = new HashMap<>();

      for (Expense e : expenses) {
         String merchant = e.getMerchant() == null ? "Unknown" : e.getMerchant();
         BigDecimal amt = this.safeMoney(e.getAmount()).abs();
         totals.merge(merchant, amt, BigDecimal::add);
         counts.put(merchant, counts.getOrDefault(merchant, 0) + 1);
         catByMerchant.putIfAbsent(merchant, this.safeCategory(e.getCategory()));
      }

      List<Entry<String, BigDecimal>> sorted = totals.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).limit(5L).toList();
      BigDecimal max = sorted.isEmpty() ? BigDecimal.ONE : sorted.get(0).getValue();
      return sorted.stream().map(entry -> {
         String cat = catByMerchant.getOrDefault(entry.getKey(), "other");
         Map<String, Object> v = new HashMap<>();
         v.put("name", entry.getKey());
         v.put("category", cat);
         v.put("icon", this.categoryIcon(cat));
         v.put("amountFmt", this.formatMoney(entry.getValue()));
         v.put("count", counts.getOrDefault(entry.getKey(), 0));
         v.put("pct", this.toPercent(entry.getValue(), max));
         return v;
      }).toList();
   }

   private Map<String, BigDecimal> spentByCategory(List<Expense> expenses) {
      return expenses.stream()
         .collect(
            Collectors.groupingBy(
               e -> this.safeCategory(e.getCategory()), Collectors.reducing(BigDecimal.ZERO, e -> this.safeMoney(e.getAmount()).abs(), BigDecimal::add)
            )
         );
   }

   private ExpenseRequest defaultExpenseForm() {
      ExpenseRequest r = new ExpenseRequest();
      r.setDate(LocalDate.now());
      r.setAccount("default");
      return r;
   }

   private ExpenseRequest toExpenseRequest(Expense e) {
      ExpenseRequest r = new ExpenseRequest();
      r.setId(e.getId());
      r.setMerchant(e.getMerchant());
      r.setAmount(e.getAmount() == null ? BigDecimal.ZERO : e.getAmount().abs());
      r.setCategory(e.getCategory());
      r.setDate(e.getDate());
      r.setNotes(e.getNote());
      r.setTags(e.getTags());
      r.setAccount("default");
      r.setRecurring(false);
      return r;
   }

   private boolean matchesFilter(Expense e, String q, String category, BigDecimal min, BigDecimal max) {
      if (category != null && !category.isBlank() && !this.safeCategory(e.getCategory()).equalsIgnoreCase(category.trim())) {
         return false;
      } else {
         if (q != null && !q.isBlank()) {
            String haystack = ((e.getMerchant() == null ? "" : e.getMerchant()) + " " + (e.getNote() == null ? "" : e.getNote())).toLowerCase(Locale.ROOT);
            if (!haystack.contains(q.toLowerCase(Locale.ROOT))) {
               return false;
            }
         }

         BigDecimal abs = e.getAmount() == null ? BigDecimal.ZERO : e.getAmount().abs();
         return min != null && abs.compareTo(min) < 0 ? false : max == null || abs.compareTo(max) <= 0;
      }
   }

   private Map<String, Object> toRecentTxnView(Expense e) {
      Map<String, Object> row = new HashMap<>();
      String cat = this.safeCategory(e.getCategory());
      row.put("category", cat);
      row.put("icon", this.categoryIcon(cat));
      row.put("title", e.getMerchant());
      row.put("meta", e.getDate() == null ? "" : e.getDate().format(SHORT_DATE));
      row.put("amount", this.safeMoney(e.getAmount()).doubleValue());
      row.put("amountFmt", this.formatSignedMoney(e.getAmount()));
      return row;
   }

   private Map<String, Object> toExpenseRowView(Expense e) {
      Map<String, Object> row = new HashMap<>();
      String cat = this.safeCategory(e.getCategory());
      row.put("id", e.getId());
      row.put("merchant", e.getMerchant());
      row.put("note", e.getNote() == null ? "" : e.getNote());
      row.put("category", cat);
      row.put("categoryLabel", this.categoryLabel(cat));
      row.put("icon", this.categoryIcon(cat));
      row.put("dateFmt", e.getDate() == null ? "" : e.getDate().format(SHORT_DATE));
      row.put("tags", this.splitTags(e.getTags()));
      row.put("amount", this.safeMoney(e.getAmount()).doubleValue());
      row.put("amountFmt", this.formatSignedMoney(e.getAmount()));
      return row;
   }

   private List<String> splitTags(String tags) {
      return tags != null && !tags.isBlank() ? Arrays.stream(tags.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList() : List.of();
   }

   private BigDecimal spentForCategoryAndPeriod(List<Expense> expenses, String category, String period) {
      LocalDate start = this.periodStart(period);
      LocalDate end = LocalDate.now();
      return expenses.stream()
         .filter(e -> this.safeCategory(e.getCategory()).equals(this.safeCategory(category)))
         .filter(e -> e.getDate() != null)
         .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
         .map(Expense::getAmount)
         .filter(Objects::nonNull)
         .map(BigDecimal::abs)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   private LocalDate periodStart(String period) {
      LocalDate today = LocalDate.now();
      String normalized = period == null ? "monthly" : period.trim().toLowerCase(Locale.ROOT);

      return switch (normalized) {
         case "weekly" -> today.with(DayOfWeek.MONDAY);
         case "yearly" -> today.withDayOfYear(1);
         default -> today.withDayOfMonth(1);
      };
   }

   private List<Expense> filterExpensesByMonth(List<Expense> expenses, String selectedMonth) {
      try {
         YearMonth selected = YearMonth.parse(selectedMonth, MONTH_YEAR);
         return expenses.stream().filter(e -> e.getDate() != null).filter(e -> YearMonth.from(e.getDate()).equals(selected)).toList();
      } catch (DateTimeParseException var4) {
         return expenses;
      }
   }

   private List<Map<String, String>> categoryOptions() {
      return List.of(
         this.option("food", "Food & Dining"),
         this.option("transport", "Transport"),
         this.option("shopping", "Shopping"),
         this.option("bills", "Bills & Utilities"),
         this.option("entertainment", "Entertainment"),
         this.option("health", "Health"),
         this.option("education", "Education"),
         this.option("other", "Other")
      );
   }

   private List<Map<String, String>> accountOptions() {
      return List.of(this.option("default", "Default"), this.option("cash", "Cash"), this.option("bank", "Bank Account"), this.option("credit", "Credit Card"));
   }

   private Map<String, String> option(String key, String label) {
      Map<String, String> m = new LinkedHashMap<>();
      m.put("key", key);
      m.put("label", label);
      m.put("id", key);
      m.put("name", label);
      return m;
   }

   private String formatMoney(BigDecimal amount) {
      BigDecimal safe = this.safeMoney(amount).abs().setScale(0, RoundingMode.HALF_UP);
      NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"));
      nf.setMaximumFractionDigits(0);
      return "₹" + nf.format(safe);
   }

   private String formatSignedMoney(BigDecimal amount) {
      BigDecimal safe = this.safeMoney(amount);
      String prefix = safe.signum() < 0 ? "-" : "+";
      return prefix + this.formatMoney(safe.abs());
   }

   private int toPercent(BigDecimal value, BigDecimal total) {
      if (total != null && total.compareTo(BigDecimal.ZERO) > 0) {
         int pct = value.multiply(new BigDecimal("100")).divide(total, 0, RoundingMode.HALF_UP).intValue();
         return Math.max(0, Math.min(pct, 100));
      } else {
         return 0;
      }
   }

   private BigDecimal sumAbs(List<Expense> expenses) {
      return expenses.stream().map(Expense::getAmount).filter(Objects::nonNull).map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   private BigDecimal safeMoney(BigDecimal v) {
      return v == null ? BigDecimal.ZERO : v;
   }

   private String safeCategory(String cat) {
      return cat != null && !cat.isBlank() ? cat.trim().toLowerCase(Locale.ROOT) : "other";
   }

   private String categoryLabel(String cat) {
      String var2 = this.safeCategory(cat);

      return switch (var2) {
         case "food" -> "Food & Dining";
         case "transport" -> "Transport";
         case "shopping" -> "Shopping";
         case "bills" -> "Bills";
         case "entertainment" -> "Entertainment";
         case "health" -> "Health";
         case "education" -> "Education";
         default -> "Other";
      };
   }

   private String categoryIcon(String cat) {
      String var2 = this.safeCategory(cat);

      return switch (var2) {
         case "food" -> "\ud83c\udf54";
         case "transport" -> "\ud83d\ude97";
         case "shopping" -> "\ud83d\udecd️";
         case "bills" -> "\ud83d\udca1";
         case "entertainment" -> "\ud83c\udfac";
         case "health" -> "\ud83c\udfe5";
         case "education" -> "\ud83d\udcda";
         default -> "\ud83d\udccc";
      };
   }

   private String categoryBgClass(String cat) {
      String var2 = this.safeCategory(cat);

      return switch (var2) {
         case "food" -> "cat-food";
         case "transport" -> "cat-transport";
         case "shopping" -> "cat-shopping";
         case "bills" -> "cat-bills";
         case "entertainment" -> "cat-entertainment";
         case "health" -> "cat-health";
         case "education" -> "cat-education";
         default -> "cat-other";
      };
   }

   private String greeting() {
      int h = LocalTime.now().getHour();
      if (h < 12) {
         return "Good morning";
      } else {
         return h < 17 ? "Good afternoon" : "Good evening";
      }
   }
}

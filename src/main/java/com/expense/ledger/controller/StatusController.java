package com.expense.ledger.controller;

import com.expense.ledger.repository.UserRepository;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatusController {
   private final UserRepository userRepository;

   public StatusController(UserRepository var1) {
      this.userRepository = var1;
   }

   @GetMapping("/status")
   public String status(Model var1) {
      boolean var2 = false;
      String var3 = "Unavailable";
      long var4 = 0L;

      try {
         var4 = this.userRepository.count();
         var2 = true;
         var3 = "Connected";
      } catch (Exception var26) {
         var3 = "Error: " + var26.getMessage();
      }

      MemoryMXBean var6 = ManagementFactory.getMemoryMXBean();
      MemoryUsage var7 = var6.getHeapMemoryUsage();
      long var8 = var7.getUsed() / 1048576L;
      long var10 = var7.getMax() / 1048576L;
      long var12 = var10 > 0L ? var8 * 100L / var10 : 0L;
      long var14 = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
      long var16 = var14 / 3600L;
      long var18 = var14 % 3600L / 60L;
      long var20 = var14 % 60L;
      String var22 = String.format("%dh %02dm %02ds", var16, var18, var20);
      String var23 = System.getProperty("java.version", "unknown");
      String var24 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      var1.addAttribute("overallOk", var2);
      var1.addAttribute("dbOk", var2);
      var1.addAttribute("dbMessage", var3);
      var1.addAttribute("userCount", var4);
      var1.addAttribute("usedMb", var8);
      var1.addAttribute("maxMb", var10);
      var1.addAttribute("memPercent", var12);
      var1.addAttribute("uptime", var22);
      var1.addAttribute("javaVersion", var23);
      var1.addAttribute("timestamp", var24);
      return "status";
   }
}

package com.expense.ledger.controller;

import com.expense.ledger.model.User;
import com.expense.ledger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final UserService userService;

    public BillingController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Map<String, Object>> upgradeToPro(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        }

        User user = userOpt.get();
        user.setPlanName("Pro");
        user.setPlanUpdatedAt(LocalDateTime.now());
        userService.save(user);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Successfully upgraded to Pro plan",
            "plan", user.getPlanName(),
            "updatedAt", user.getPlanUpdatedAt().toString()
        ));
    }
}

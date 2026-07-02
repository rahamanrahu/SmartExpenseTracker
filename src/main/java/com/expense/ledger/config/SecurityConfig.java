package com.expense.ledger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

   @Value("${ledger.remember-me.key:ledger-remember-me-change-me-to-a-secret}")
   private String rememberMeKey;

   /**
    * Prevent Spring Boot from auto-registering AuthRateLimitFilter in the
    * default servlet filter chain. It is registered exclusively inside the
    * Spring Security filter chain via addFilterBefore below.
    * Without this, the filter runs twice per request — once outside security
    * (no Authentication present) and once inside — causing broken auth state.
    */
   @Bean
   public FilterRegistrationBean<AuthRateLimitFilter> rateLimitFilterRegistration(
         AuthRateLimitFilter filter) {
      FilterRegistrationBean<AuthRateLimitFilter> reg = new FilterRegistrationBean<>(filter);
      reg.setEnabled(false);
      return reg;
   }

   /**
    * Same reason — prevent double-registration of TwoFactorSessionGuardFilter.
    * When it ran twice, the second pass (before SecurityContext was populated)
    * would wrongly redirect authenticated users back to /2fa-challenge.
    */
   @Bean
   public FilterRegistrationBean<TwoFactorSessionGuardFilter> twoFactorGuardRegistration(
         TwoFactorSessionGuardFilter filter) {
      FilterRegistrationBean<TwoFactorSessionGuardFilter> reg = new FilterRegistrationBean<>(filter);
      reg.setEnabled(false);
      return reg;
   }

   @Bean
   public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      AuthRateLimitFilter rateLimitFilter,
      TwoFactorAuthenticationSuccessHandler successHandler,
      TwoFactorSessionGuardFilter twoFactorGuard
   ) throws Exception {
      http
         .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
         .addFilterAfter(twoFactorGuard, UsernamePasswordAuthenticationFilter.class)
         .headers(h -> h
            .frameOptions(f -> f.sameOrigin())
            .referrerPolicy(r -> r.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .contentSecurityPolicy(csp -> csp.policyDirectives(
               "default-src 'self'; " +
               "script-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net https://cdn.tailwindcss.com; " +
               "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
               "font-src 'self' https://fonts.gstatic.com; " +
               "img-src 'self' data: https:; " +
               "connect-src 'self';"
            ))
         )
         .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers("/api/**", "/ai/**", "/sse/**", "/api/billing/**", "/api/voice/**", "/api/receipt/**")
         )
         .formLogin(form -> form
            .loginPage("/login")
            .successHandler(successHandler)
            .permitAll()
         )
         .rememberMe(rm -> rm
            .key(rememberMeKey)
            .tokenValiditySeconds(60 * 60 * 24 * 30)
            .rememberMeParameter("remember-me")
            .rememberMeCookieName("LEDGER_REMEMBER")
            .useSecureCookie(false)
         )
         .logout(out -> out
            .logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID", "LEDGER_REMEMBER")
            .permitAll()
         )
         .authorizeHttpRequests(reg -> reg
            .requestMatchers(
               "/",
               "/signup",
               "/forgot-password",
               "/reset-password",
               "/status",
               "/css/**",
               "/js/**",
               "/images/**",
               "/webjars/**",
               "/favicon.ico",
               "/favicon.svg",
               "/fonts/**",
               "/error",
               "/error/**"
            )
            .permitAll()
            .anyRequest()
            .authenticated()
         );
      return http.build();
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}

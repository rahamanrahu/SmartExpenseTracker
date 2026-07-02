package com.expense.ledger.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
   private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

   @ExceptionHandler(NoHandlerFoundException.class)
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public ModelAndView handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
      log.warn("404 error: {}", ex.getMessage());
      ModelAndView mv = new ModelAndView("error/404");
      mv.addObject("message", "The page you requested could not be found.");
      mv.addObject("status", HttpStatus.NOT_FOUND.value());
      mv.addObject("path", request.getRequestURI());
      return mv;
   }

   /**
    * Firefox "Resend" after a POST can arrive without a fresh CSRF token, or an expired
    * session can cause 403. Treat these as routine — silently redirect the user back to
    * a safe GET page instead of showing an angry error page.
    */
   @ExceptionHandler({CsrfException.class, AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class})
   public void handleAccessOrCsrf(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
      log.info("Access/CSRF issue on {} — recovering: {}", request.getRequestURI(), ex.getMessage());
      String target = safeRedirectTarget(request);
      if (!response.isCommitted()) {
         response.sendRedirect(request.getContextPath() + target);
      }
   }

   @ExceptionHandler(AuthenticationException.class)
   public void handleAuthException(AuthenticationException ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
      log.info("Authentication required for {}: {}", request.getRequestURI(), ex.getMessage());
      if (!response.isCommitted()) {
         response.sendRedirect(request.getContextPath() + "/login");
      }
   }

   @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
   public String handleValidationException(Exception ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
      log.warn("Validation error on {}: {}", request.getRequestURI(), ex.getMessage());
      redirectAttributes.addFlashAttribute("flashError", "Please check your input and try again.");
      String referer = request.getHeader("referer");
      return referer != null && !referer.isBlank() ? "redirect:" + referer : "redirect:/dashboard";
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public String handleIllegalArg(IllegalArgumentException ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
      log.warn("Illegal argument on {}: {}", request.getRequestURI(), ex.getMessage());
      redirectAttributes.addFlashAttribute("flashError", ex.getMessage() != null ? ex.getMessage() : "Invalid request.");
      String referer = request.getHeader("referer");
      return referer != null && !referer.isBlank() ? "redirect:" + referer : "redirect:/dashboard";
   }

   @ExceptionHandler(DataAccessException.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ModelAndView handleDatabaseException(DataAccessException ex, HttpServletRequest request) {
      log.error("Database error on {}", request.getRequestURI(), ex);
      return internalErrorView("We had trouble reading your data. Please try again.", request);
   }

   @ExceptionHandler(NullPointerException.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ModelAndView handleNullPointer(NullPointerException ex, HttpServletRequest request) {
      log.error("Null pointer exception on {}", request.getRequestURI(), ex);
      return internalErrorView("Something went wrong. Please try again.", request);
   }

   @ExceptionHandler(RuntimeException.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
      log.error("Runtime exception on {}", request.getRequestURI(), ex);
      return internalErrorView("Something went wrong. Please try again.", request);
   }

   @ExceptionHandler(Exception.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ModelAndView handleAnyException(Exception ex, HttpServletRequest request) {
      log.error("Unhandled exception on {}", request.getRequestURI(), ex);
      return internalErrorView("Something went wrong. Please try again.", request);
   }

   private ModelAndView internalErrorView(String message, HttpServletRequest request) {
      ModelAndView mv = new ModelAndView("error/500");
      mv.addObject("message", message);
      mv.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      mv.addObject("path", request.getRequestURI());
      return mv;
   }

   private String safeRedirectTarget(HttpServletRequest request) {
      String referer = request.getHeader("referer");
      if (referer != null && !referer.isBlank()) {
         int qIdx = referer.indexOf('?');
         String path = qIdx > 0 ? referer.substring(0, qIdx) : referer;
         int schemeIdx = path.indexOf("://");
         if (schemeIdx > 0) {
            int pathStart = path.indexOf('/', schemeIdx + 3);
            if (pathStart > 0) {
               String relative = path.substring(pathStart);
               if (!relative.equals("/login") && !relative.startsWith("/2fa")) {
                  return relative;
               }
            }
         }
      }
      return "/dashboard";
   }
}

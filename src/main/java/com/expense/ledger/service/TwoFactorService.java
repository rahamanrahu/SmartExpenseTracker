package com.expense.ledger.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {
   private static final String ISSUER = "Ledger";
   private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

   public String generateSecret() {
      GoogleAuthenticatorKey key = this.googleAuthenticator.createCredentials();
      return key.getKey();
   }

   public boolean verifyCode(String secret, String code) {
      if (secret != null && !secret.isBlank() && code != null && !code.isBlank()) {
         String normalized = code.trim();
         if (!normalized.matches("\\d{6}")) {
            return false;
         } else {
            try {
               int pin = Integer.parseInt(normalized);
               return this.googleAuthenticator.authorize(secret, pin);
            } catch (NumberFormatException var5) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public String buildOtpAuthUri(String email, String secret) {
      String account = email == null ? "user" : email.trim().toLowerCase();
      return "otpauth://totp/" + this.urlEncode(ISSUER + ":" + account) + "?secret=" + this.urlEncode(secret) + "&issuer=" + this.urlEncode(ISSUER);
   }

   public String buildQrCodeDataUri(String otpauthUri) {
      if (otpauthUri != null && !otpauthUri.isBlank()) {
         try {
            BitMatrix matrix = new MultiFormatWriter().encode(otpauthUri, BarcodeFormat.QR_CODE, 220, 220);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", out);
            String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
            return "data:image/png;base64," + base64;
         } catch (IOException | WriterException var6) {
            return "";
         }
      } else {
         return "";
      }
   }

   private String urlEncode(String value) {
      return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
   }
}

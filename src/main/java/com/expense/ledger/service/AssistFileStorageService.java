package com.expense.ledger.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AssistFileStorageService {

   private final Path uploadsRoot;

   public AssistFileStorageService(@Value("${ledger.data.dir:${user.home}/.ledger}") String dataDir) {
      this.uploadsRoot = Paths.get(dataDir).resolve("uploads").toAbsolutePath().normalize();
   }

   @SuppressWarnings("null")
   public String store(MultipartFile file, String kind) throws IOException {
      String safeKind = (kind == null || kind.isBlank()) ? "misc" : kind.replaceAll("[^a-zA-Z0-9_-]", "");
      String ext = extensionOf(file.getOriginalFilename());
      String dateFolder = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
      String fileName = UUID.randomUUID() + ext;
      Path targetDir = uploadsRoot.resolve(safeKind).resolve(dateFolder).normalize();
      if (!targetDir.startsWith(uploadsRoot)) {
         throw new IOException("Invalid upload target path");
      }
      Files.createDirectories(targetDir);
      Path target = targetDir.resolve(fileName).normalize();
      file.transferTo(target.toFile());
      return target.toString();
   }

   private String extensionOf(String original) {
      if (original == null || original.isBlank()) {
         return "";
      }
      int idx = original.lastIndexOf('.');
      if (idx < 0 || idx == original.length() - 1) {
         return "";
      }
      String ext = original.substring(idx).toLowerCase();
      return ext.length() <= 10 ? ext : "";
   }
}

package com.expense.ledger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LedgerApplication {
   public static void main(String[] args) throws Exception {
      Path dataDir = resolveDataDir();
      Files.createDirectories(dataDir);
      System.setProperty("ledger.data.dir.resolved", dataDir.toString());
      SpringApplication.run(LedgerApplication.class, args);
   }

   private static Path resolveDataDir() {
      String fromEnv = System.getenv("LEDGER_DATA_DIR");
      if (fromEnv != null && !fromEnv.isBlank()) {
         return Paths.get(fromEnv).toAbsolutePath().normalize();
      } else {
         String fromProp = System.getProperty("LEDGER_DATA_DIR");
         return fromProp != null && !fromProp.isBlank()
            ? Paths.get(fromProp).toAbsolutePath().normalize()
            : Paths.get(System.getProperty("user.home"), ".ledger").toAbsolutePath().normalize();
      }
   }
}

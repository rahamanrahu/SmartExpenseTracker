package com.expense.ledger.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AppConfig {
   private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
   @Value("${ledger.data.dir}")
   private String dataDir;

   @PostConstruct
   void logDataDir() {
      log.info("Ledger data directory: {}", this.dataDir);
      log.info("Account data persists across restarts at: {}/ledgerdb.mv.db", this.dataDir);
   }
}

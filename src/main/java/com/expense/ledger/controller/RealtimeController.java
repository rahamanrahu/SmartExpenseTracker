package com.expense.ledger.controller;

import com.expense.ledger.repository.UserRepository;
import com.expense.ledger.service.AiService;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Server-Sent Events endpoint that streams a live status snapshot every 3 seconds
 * so the status page / dashboard can update without polling.
 */
@RestController
@RequestMapping("/sse")
public class RealtimeController {

   private static final Logger log = LoggerFactory.getLogger(RealtimeController.class);
   private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final ScheduledExecutorService scheduler =
      Executors.newScheduledThreadPool(2, r -> {
         Thread t = new Thread(r, "ledger-sse");
         t.setDaemon(true);
         return t;
      });

   private final UserRepository userRepository;
   private final AiService aiService;

   public RealtimeController(UserRepository userRepository, AiService aiService) {
      this.userRepository = userRepository;
      this.aiService = aiService;
   }

   @GetMapping(path = "/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   public SseEmitter streamStatus() {
      SseEmitter emitter = new SseEmitter(Duration.ofMinutes(10).toMillis());

      Runnable tick = () -> {
         try {
            emitter.send(SseEmitter.event()
               .name("status")
               .data((Object)Objects.requireNonNull(currentSnapshot()), MediaType.APPLICATION_JSON));
         } catch (IOException ex) {
            emitter.complete();
         } catch (Exception ex) {
            log.debug("SSE tick failed, completing: {}", ex.getMessage());
            emitter.complete();
         }
      };

      // Send one immediately so the client doesn't wait 3s for the first update
      tick.run();
      var handle = scheduler.scheduleAtFixedRate(tick, 3, 3, TimeUnit.SECONDS);

      emitter.onCompletion(() -> handle.cancel(true));
      emitter.onTimeout(() -> handle.cancel(true));
      emitter.onError(ex -> handle.cancel(true));
      return emitter;
   }

   private Map<String, Object> currentSnapshot() {
      Map<String, Object> snap = new HashMap<>();

      boolean dbOk = false;
      String dbMessage = "Unavailable";
      long userCount = 0;
      try {
         userCount = userRepository.count();
         dbOk = true;
         dbMessage = "Connected";
      } catch (Exception ex) {
         dbMessage = "Error: " + ex.getClass().getSimpleName();
      }

      MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
      MemoryUsage heap = memBean.getHeapMemoryUsage();
      long usedMb = heap.getUsed() / 1_048_576L;
      long maxMb  = heap.getMax()  / 1_048_576L;
      long memPercent = (maxMb > 0) ? usedMb * 100L / maxMb : 0L;

      long uptimeSec = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
      long h = uptimeSec / 3600L;
      long m = (uptimeSec % 3600L) / 60L;
      long s = uptimeSec % 60L;
      String uptime = String.format("%dh %02dm %02ds", h, m, s);

      snap.put("dbOk", dbOk);
      snap.put("dbMessage", dbMessage);
      snap.put("userCount", userCount);
      snap.put("usedMb", usedMb);
      snap.put("maxMb", maxMb);
      snap.put("memPercent", memPercent);
      snap.put("uptime", uptime);
      snap.put("javaVersion", System.getProperty("java.version", "unknown"));
      snap.put("timestamp", LocalDateTime.now().format(TS));
      snap.put("ai", aiService.providerStatus());
      snap.put("overallOk", dbOk && memPercent < 90);
      return snap;
   }
}

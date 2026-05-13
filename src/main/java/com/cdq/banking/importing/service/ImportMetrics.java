package com.cdq.banking.importing.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class ImportMetrics {

  private final Counter successCounter;
  private final Counter failureCounter;
  private final Timer processingTimer;

  public ImportMetrics(MeterRegistry registry) {
    this.successCounter =
        Counter.builder("import.jobs")
            .tag("result", "success")
            .description("Number of successfully completed import jobs")
            .register(registry);
    this.failureCounter =
        Counter.builder("import.jobs")
            .tag("result", "failure")
            .description("Number of failed import jobs")
            .register(registry);
    this.processingTimer =
        Timer.builder("import.processing.duration")
            .description("Time spent processing an import job")
            .register(registry);
  }

  public void recordSuccess() {
    successCounter.increment();
  }

  public void recordFailure() {
    failureCounter.increment();
  }

  public void recordDuration(Runnable task) {
    processingTimer.record(task);
  }
}

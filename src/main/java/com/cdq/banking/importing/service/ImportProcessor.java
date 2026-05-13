package com.cdq.banking.importing.service;

import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.ParseResult;
import com.cdq.banking.importing.domain.model.Transaction;
import com.cdq.banking.importing.domain.port.ImportJobRepository;
import com.cdq.banking.importing.domain.port.TransactionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportProcessor {

  private final TransactionRepository transactionRepository;
  private final ImportJobRepository importJobRepository;
  private final ImportMetrics metrics;

  @Async("importExecutor")
  public void process(String jobId, ParseResult parseResult) {
    ImportJob job =
        importJobRepository
            .findById(jobId)
            .orElseThrow(() -> new IllegalStateException("Import job not found: " + jobId));

    metrics.recordDuration(() -> doProcess(job, parseResult));
  }

  private void doProcess(ImportJob job, ParseResult result) {
    try {
      job.startProcessing();
      importJobRepository.save(job);

      List<Transaction> transactions =
          result.transactions().stream().map(tx -> tx.withImportJobId(job.getId())).toList();
      transactionRepository.saveAll(transactions);

      job.complete(
          result.totalRowCount(),
          result.validRowCount(),
          result.invalidRowCount(),
          result.rowErrors());
      importJobRepository.save(job);
      metrics.recordSuccess();

      log.info(
          "Import job {} completed: {} total, {} valid, {} invalid",
          job.getId(),
          result.totalRowCount(),
          result.validRowCount(),
          result.invalidRowCount());
    } catch (Exception e) {
      job.fail(e.getMessage());
      importJobRepository.save(job);
      metrics.recordFailure();
      log.error("Import job {} failed", job.getId(), e);
    }
  }
}

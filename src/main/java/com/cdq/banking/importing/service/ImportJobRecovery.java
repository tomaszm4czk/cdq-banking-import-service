package com.cdq.banking.importing.service;

import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.ImportStatus;
import com.cdq.banking.importing.domain.port.ImportJobRepository;
import com.cdq.banking.importing.domain.port.TransactionRepository;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportJobRecovery {

  private final ImportJobRepository importJobRepository;
  private final TransactionRepository transactionRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void recoverStuckJobs() {
    List<ImportJob> stuckJobs =
        Stream.concat(
                importJobRepository.findByStatus(ImportStatus.PENDING).stream(),
                importJobRepository.findByStatus(ImportStatus.PROCESSING).stream())
            .toList();

    if (stuckJobs.isEmpty()) {
      return;
    }

    log.info("Marking {} stuck import jobs as failed", stuckJobs.size());

    for (ImportJob job : stuckJobs) {
      transactionRepository.deleteByImportJobId(job.getId());
      job.fail("Job was interrupted during processing");
      importJobRepository.save(job);
    }
  }
}

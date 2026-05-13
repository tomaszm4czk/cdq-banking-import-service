package com.cdq.banking.importing.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.ParseResult;
import com.cdq.banking.importing.domain.model.Transaction;
import com.cdq.banking.importing.domain.port.ImportJobRepository;
import com.cdq.banking.importing.domain.port.TransactionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ImportProcessorTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private ImportJobRepository importJobRepository;

  @Mock private ImportMetrics metrics;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private ImportProcessor processor;

  @BeforeEach
  void setUp() {
    doAnswer(
            inv -> {
              ((Runnable) inv.getArgument(0)).run();
              return null;
            })
        .when(metrics)
        .recordDuration(any());
  }

  @Test
  void shouldProcessFileSuccessfully() {
    ImportJob job = ImportJob.create("test.csv");
    when(importJobRepository.findById(job.getId())).thenReturn(Optional.of(job));

    Transaction mockTx = mock(Transaction.class);
    when(mockTx.withImportJobId(any())).thenReturn(mockTx);
    ParseResult result = new ParseResult(List.of(mockTx), 1, List.of());

    processor.process(job.getId(), result);

    verify(importJobRepository, times(2)).save(any(ImportJob.class));
    verify(transactionRepository).saveAll(anyList());
    assertThat(job.getStatus().name()).isEqualTo("COMPLETED");
  }

  @Test
  void shouldMarkJobAsFailedOnException() {
    ImportJob job = ImportJob.create("test.csv");
    when(importJobRepository.findById(job.getId())).thenReturn(Optional.of(job));
    doThrow(new RuntimeException("DB error")).when(transactionRepository).saveAll(anyList());

    Transaction mockTx = mock(Transaction.class);
    when(mockTx.withImportJobId(any())).thenReturn(mockTx);
    ParseResult result = new ParseResult(List.of(mockTx), 1, List.of());

    processor.process(job.getId(), result);

    assertThat(job.getStatus().name()).isEqualTo("FAILED");
  }
}

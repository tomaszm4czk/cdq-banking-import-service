package com.cdq.banking.importing.domain.model;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportJob {
  private final String id;
  private final String filename;
  private final Instant createdAt;
  private ImportStatus status;
  private int totalRows;
  private int validRows;
  private int invalidRows;
  private List<RowError> rowErrors;
  private String errorMessage;

  public static ImportJob create(String filename) {
    if (filename == null || filename.isBlank()) {
      throw new DomainValidationException("Filename must not be null or blank");
    }
    return new ImportJob(
        UUID.randomUUID().toString(),
        filename,
        Instant.now(),
        ImportStatus.PENDING,
        0,
        0,
        0,
        List.of(),
        null);
  }

  public static ImportJob reconstitute(
      String id,
      String filename,
      Instant createdAt,
      ImportStatus status,
      int totalRows,
      int validRows,
      int invalidRows,
      List<RowError> rowErrors,
      String errorMessage) {
    return new ImportJob(
        id,
        filename,
        createdAt,
        status,
        totalRows,
        validRows,
        invalidRows,
        rowErrors != null ? rowErrors : List.of(),
        errorMessage);
  }

  public void startProcessing() {
    this.status = ImportStatus.PROCESSING;
  }

  public void complete(int totalRows, int validRows, int invalidRows, List<RowError> rowErrors) {
    this.totalRows = totalRows;
    this.validRows = validRows;
    this.invalidRows = invalidRows;
    this.rowErrors = rowErrors != null ? rowErrors : List.of();
    this.status = ImportStatus.COMPLETED;
  }

  public void fail(String errorMessage) {
    this.errorMessage = errorMessage;
    this.status = ImportStatus.FAILED;
  }

  public boolean isResultAvailable() {
    return this.status == ImportStatus.COMPLETED;
  }
}

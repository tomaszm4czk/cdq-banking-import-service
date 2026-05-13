package com.cdq.banking.importing.domain.exception;

public class ImportJobNotFoundException extends RuntimeException {

  public ImportJobNotFoundException(String jobId) {
    super("Import job not found: " + jobId);
  }
}

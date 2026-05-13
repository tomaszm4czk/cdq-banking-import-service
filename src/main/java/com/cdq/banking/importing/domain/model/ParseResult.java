package com.cdq.banking.importing.domain.model;

import java.util.List;

public record ParseResult(
    List<Transaction> transactions, int totalRowCount, List<RowError> rowErrors) {
  public int validRowCount() {
    return totalRowCount - rowErrors.size();
  }

  public int invalidRowCount() {
    return rowErrors.size();
  }
}

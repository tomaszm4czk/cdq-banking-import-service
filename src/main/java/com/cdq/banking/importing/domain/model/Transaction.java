package com.cdq.banking.importing.domain.model;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import java.time.LocalDate;
import java.util.UUID;

public record Transaction(
    String id,
    Iban iban,
    LocalDate date,
    Money money,
    TransactionCategory category,
    String importJobId) {

  public Transaction {
    if (iban == null) throw new DomainValidationException("IBAN must not be null");
    if (date == null) throw new DomainValidationException("Date must not be null");
    if (money == null) throw new DomainValidationException("Money must not be null");
    if (category == null) throw new DomainValidationException("Category must not be null");
  }

  public static Transaction create(
      Iban iban, LocalDate date, Money money, TransactionCategory category) {
    return new Transaction(UUID.randomUUID().toString(), iban, date, money, category, null);
  }

  public Transaction withImportJobId(String importJobId) {
    return new Transaction(this.id, this.iban, this.date, this.money, this.category, importJobId);
  }
}

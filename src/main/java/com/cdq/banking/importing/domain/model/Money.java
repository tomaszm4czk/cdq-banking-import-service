package com.cdq.banking.importing.domain.model;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

  public Money {
    if (amount == null) {
      throw new DomainValidationException("Amount must not be null");
    }
    if (currency == null) {
      throw new DomainValidationException("Currency must not be null");
    }
  }

  public static Money of(BigDecimal amount, Currency currency) {
    return new Money(amount, currency);
  }
}

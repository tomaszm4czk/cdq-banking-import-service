package com.cdq.banking.importing.domain.model;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import java.util.regex.Pattern;

public record Iban(String value) {

  private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z0-9]{11,30}$");

  public Iban {
    if (value == null || value.isBlank()) {
      throw new DomainValidationException("IBAN must not be null or blank");
    }
    value = value.trim();
    if (!IBAN_PATTERN.matcher(value).matches()) {
      throw new DomainValidationException("Invalid IBAN format: " + value);
    }
  }

  public static Iban of(String value) {
    return new Iban(value);
  }
}

package com.cdq.banking.importing.domain.validation;

import java.util.List;

public record ValidationResult(List<String> errors) {

  public static ValidationResult valid() {
    return new ValidationResult(List.of());
  }

  public static ValidationResult invalid(List<String> errors) {
    return new ValidationResult(List.copyOf(errors));
  }

  public boolean isValid() {
    return errors.isEmpty();
  }
}

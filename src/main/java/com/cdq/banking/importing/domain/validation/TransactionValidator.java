package com.cdq.banking.importing.domain.validation;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import com.cdq.banking.importing.domain.model.Iban;
import com.cdq.banking.importing.domain.model.TransactionCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public class TransactionValidator {

  private static final List<DateTimeFormatter> DATE_FORMATTERS =
      List.of(
          DateTimeFormatter.ISO_LOCAL_DATE,
          DateTimeFormatter.ofPattern("dd/MM/yyyy"),
          DateTimeFormatter.ofPattern("dd.MM.yyyy"),
          DateTimeFormatter.ofPattern("dd-MM-yyyy"));

  public static LocalDate parseDate(String date) {
    String trimmed = date.trim();
    for (DateTimeFormatter formatter : DATE_FORMATTERS) {
      try {
        return LocalDate.parse(trimmed, formatter);
      } catch (DateTimeParseException ignored) {
      }
    }
    throw new DateTimeParseException("Unsupported date format", trimmed, 0);
  }

  public ValidationResult validate(Map<String, String> row) {
    List<String> errors = new ArrayList<>();

    validateIban(row.getOrDefault("iban", ""), errors);
    validateDate(row.getOrDefault("date", ""), errors);
    validateCurrency(row.getOrDefault("currency", ""), errors);
    validateCategory(row.getOrDefault("category", ""), errors);
    validateAmount(row.getOrDefault("amount", ""), errors);

    return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
  }

  private void validateIban(String iban, List<String> errors) {
    try {
      Iban.of(iban);
    } catch (DomainValidationException e) {
      errors.add("Invalid IBAN: " + e.getMessage());
    }
  }

  private void validateDate(String date, List<String> errors) {
    try {
      parseDate(date);
    } catch (DateTimeParseException | NullPointerException e) {
      errors.add(
          "Invalid date format (supported: yyyy-MM-dd, dd/MM/yyyy, dd.MM.yyyy, dd-MM-yyyy): "
              + date);
    }
  }

  private void validateCurrency(String currency, List<String> errors) {
    try {
      Currency.getInstance(currency.trim());
    } catch (IllegalArgumentException | NullPointerException e) {
      errors.add("Invalid currency code: " + currency);
    }
  }

  private void validateCategory(String category, List<String> errors) {
    try {
      TransactionCategory.valueOf(category.trim());
    } catch (IllegalArgumentException | NullPointerException e) {
      errors.add("Unknown category: " + category);
    }
  }

  private void validateAmount(String amount, List<String> errors) {
    try {
      new BigDecimal(amount.trim());
    } catch (NumberFormatException | NullPointerException e) {
      errors.add("Invalid amount (expected number): " + amount);
    }
  }
}

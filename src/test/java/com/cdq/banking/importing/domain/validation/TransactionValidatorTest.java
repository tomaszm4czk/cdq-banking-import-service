package com.cdq.banking.importing.domain.validation;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class TransactionValidatorTest {

  private final TransactionValidator validator = new TransactionValidator();

  @Test
  void shouldAcceptValidRow() {
    Map<String, String> row =
        Map.of(
            "iban", "DE89370400440532013000",
            "date", "2026-01-15",
            "currency", "EUR",
            "category", "GROCERIES",
            "amount", "45.99");
    ValidationResult result = validator.validate(row);
    assertThat(result.isValid()).isTrue();
    assertThat(result.errors()).isEmpty();
  }

  @Test
  void shouldRejectMissingIban() {
    Map<String, String> row =
        Map.of(
            "iban", "",
            "date", "2026-01-15",
            "currency", "EUR",
            "category", "GROCERIES",
            "amount", "45.99");
    ValidationResult result = validator.validate(row);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).anyMatch(e -> e.contains("IBAN"));
  }

  @Test
  void shouldRejectInvalidDate() {
    Map<String, String> row =
        Map.of(
            "iban", "DE89370400440532013000",
            "date", "not-a-date",
            "currency", "EUR",
            "category", "GROCERIES",
            "amount", "45.99");
    ValidationResult result = validator.validate(row);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).anyMatch(e -> e.contains("date"));
  }

  @Test
  void shouldRejectInvalidCurrency() {
    Map<String, String> row =
        Map.of(
            "iban", "DE89370400440532013000",
            "date", "2026-01-15",
            "currency", "FAKE",
            "category", "GROCERIES",
            "amount", "45.99");
    ValidationResult result = validator.validate(row);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).anyMatch(e -> e.contains("currency"));
  }

  @Test
  void shouldRejectInvalidAmount() {
    Map<String, String> row =
        Map.of(
            "iban", "DE89370400440532013000",
            "date", "2026-01-15",
            "currency", "EUR",
            "category", "GROCERIES",
            "amount", "abc");
    ValidationResult result = validator.validate(row);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).anyMatch(e -> e.contains("amount"));
  }

  @Test
  void shouldRejectUnknownCategory() {
    Map<String, String> row =
        Map.of(
            "iban", "DE89370400440532013000",
            "date", "2026-01-15",
            "currency", "EUR",
            "category", "NONEXISTENT",
            "amount", "45.99");
    ValidationResult result = validator.validate(row);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).anyMatch(e -> e.contains("category"));
  }

  @Test
  void shouldCollectMultipleErrors() {
    Map<String, String> row =
        Map.of(
            "iban", "",
            "date", "bad",
            "currency", "X",
            "category", "NOPE",
            "amount", "abc");
    ValidationResult result = validator.validate(row);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).hasSizeGreaterThanOrEqualTo(4);
  }
}

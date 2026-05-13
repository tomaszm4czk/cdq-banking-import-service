package com.cdq.banking.importing.domain.model;

import static org.assertj.core.api.Assertions.*;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class TransactionTest {

  @Test
  void shouldCreateTransaction() {
    Transaction tx =
        Transaction.create(
            Iban.of("DE89370400440532013000"),
            LocalDate.of(2026, 1, 15),
            Money.of(new BigDecimal("45.99"), Currency.getInstance("EUR")),
            TransactionCategory.GROCERIES);

    assertThat(tx.id()).isNotNull();
    assertThat(tx.iban().value()).isEqualTo("DE89370400440532013000");
    assertThat(tx.date()).isEqualTo(LocalDate.of(2026, 1, 15));
    assertThat(tx.money().amount()).isEqualByComparingTo(new BigDecimal("45.99"));
    assertThat(tx.category()).isEqualTo(TransactionCategory.GROCERIES);
  }

  @Test
  void shouldRejectNullIban() {
    assertThatThrownBy(
            () ->
                Transaction.create(
                    null,
                    LocalDate.now(),
                    Money.of(BigDecimal.TEN, Currency.getInstance("EUR")),
                    TransactionCategory.OTHER))
        .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldRejectNullDate() {
    assertThatThrownBy(
            () ->
                Transaction.create(
                    Iban.of("DE89370400440532013000"),
                    null,
                    Money.of(BigDecimal.TEN, Currency.getInstance("EUR")),
                    TransactionCategory.OTHER))
        .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldRejectNullMoney() {
    assertThatThrownBy(
            () ->
                Transaction.create(
                    Iban.of("DE89370400440532013000"),
                    LocalDate.now(),
                    null,
                    TransactionCategory.OTHER))
        .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldRejectNullCategory() {
    assertThatThrownBy(
            () ->
                Transaction.create(
                    Iban.of("DE89370400440532013000"),
                    LocalDate.now(),
                    Money.of(BigDecimal.TEN, Currency.getInstance("EUR")),
                    null))
        .isInstanceOf(DomainValidationException.class);
  }
}

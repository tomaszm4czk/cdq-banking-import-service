package com.cdq.banking.importing.domain.model;

import static org.assertj.core.api.Assertions.*;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void shouldCreateMoney() {
    Money money = Money.of(new BigDecimal("45.99"), Currency.getInstance("EUR"));
    assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("45.99"));
    assertThat(money.currency()).isEqualTo(Currency.getInstance("EUR"));
  }

  @Test
  void shouldRejectNullAmount() {
    assertThatThrownBy(() -> Money.of(null, Currency.getInstance("EUR")))
        .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldRejectNullCurrency() {
    assertThatThrownBy(() -> Money.of(BigDecimal.TEN, null))
        .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldBeEqualByValue() {
    Money a = Money.of(new BigDecimal("100.00"), Currency.getInstance("PLN"));
    Money b = Money.of(new BigDecimal("100.00"), Currency.getInstance("PLN"));
    assertThat(a).isEqualTo(b);
  }
}

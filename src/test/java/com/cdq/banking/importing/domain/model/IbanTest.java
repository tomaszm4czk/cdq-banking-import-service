package com.cdq.banking.importing.domain.model;

import static org.assertj.core.api.Assertions.*;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IbanTest {

  @Test
  void shouldCreateValidIban() {
    Iban iban = Iban.of("DE89370400440532013000");
    assertThat(iban.value()).isEqualTo("DE89370400440532013000");
  }

  @Test
  void shouldRejectNullIban() {
    assertThatThrownBy(() -> Iban.of(null))
        .isInstanceOf(DomainValidationException.class)
        .hasMessageContaining("IBAN");
  }

  @Test
  void shouldRejectBlankIban() {
    assertThatThrownBy(() -> Iban.of("  ")).isInstanceOf(DomainValidationException.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABC", "12345", "X"})
  void shouldRejectInvalidIbanFormat(String invalid) {
    assertThatThrownBy(() -> Iban.of(invalid)).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldTrimWhitespace() {
    Iban iban = Iban.of("  DE89370400440532013000  ");
    assertThat(iban.value()).isEqualTo("DE89370400440532013000");
  }

  @Test
  void shouldBeEqualByValue() {
    Iban a = Iban.of("DE89370400440532013000");
    Iban b = Iban.of("DE89370400440532013000");
    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}

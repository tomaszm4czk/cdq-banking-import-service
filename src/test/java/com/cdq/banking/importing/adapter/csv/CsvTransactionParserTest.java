package com.cdq.banking.importing.adapter.csv;

import static org.assertj.core.api.Assertions.*;

import com.cdq.banking.importing.domain.exception.FileParseException;
import com.cdq.banking.importing.domain.model.ParseResult;
import com.cdq.banking.importing.domain.model.Transaction;
import com.cdq.banking.importing.domain.model.TransactionCategory;
import com.cdq.banking.importing.domain.validation.TransactionValidator;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CsvTransactionParserTest {

  private final CsvTransactionParser parser =
      new CsvTransactionParser(new TransactionValidator(), 10_000);

  @Test
  void shouldParseValidCsv() {
    byte[] csv =
        """
                iban,date,currency,category,amount
                DE89370400440532013000,2026-01-15,EUR,GROCERIES,45.99
                PL61109010140000071219812874,2026-01-20,PLN,SALARY,3500.00
                """
            .getBytes(StandardCharsets.UTF_8);

    ParseResult result = parser.parse(csv);

    assertThat(result.transactions()).hasSize(2);
    assertThat(result.invalidRowCount()).isZero();

    Transaction first = result.transactions().getFirst();
    assertThat(first.iban().value()).isEqualTo("DE89370400440532013000");
    assertThat(first.money().amount()).isEqualByComparingTo(new BigDecimal("45.99"));
    assertThat(first.category()).isEqualTo(TransactionCategory.GROCERIES);
  }

  @Test
  void shouldSkipInvalidRowsAndContinue() {
    byte[] csv =
        """
                iban,date,currency,category,amount
                DE89370400440532013000,2026-01-15,EUR,GROCERIES,45.99
                INVALID_IBAN,bad-date,FAKE,NOPE,abc
                PL61109010140000071219812874,2026-01-20,PLN,SALARY,3500.00
                """
            .getBytes(StandardCharsets.UTF_8);

    ParseResult result = parser.parse(csv);

    assertThat(result.transactions()).hasSize(2);
    assertThat(result.invalidRowCount()).isEqualTo(1);
    assertThat(result.totalRowCount()).isEqualTo(3);
    assertThat(result.rowErrors()).hasSize(1);
    assertThat(result.rowErrors().getFirst().row()).isEqualTo(2);
    assertThat(result.rowErrors().getFirst().errors()).isNotEmpty();
  }

  @Test
  void shouldHandleEmptyCsv() {
    byte[] csv = "iban,date,currency,category,amount\n".getBytes(StandardCharsets.UTF_8);

    ParseResult result = parser.parse(csv);

    assertThat(result.transactions()).isEmpty();
    assertThat(result.totalRowCount()).isZero();
  }

  @Test
  void shouldRejectCsvWithMissingHeaders() {
    byte[] csv =
        """
                wrong,headers
                data,here
                """
            .getBytes(StandardCharsets.UTF_8);

    assertThatThrownBy(() -> parser.parse(csv))
        .isInstanceOf(FileParseException.class)
        .hasMessageContaining("header");
  }
}

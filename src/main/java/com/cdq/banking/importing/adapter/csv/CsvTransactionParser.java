package com.cdq.banking.importing.adapter.csv;

import com.cdq.banking.importing.domain.exception.FileParseException;
import com.cdq.banking.importing.domain.model.*;
import com.cdq.banking.importing.domain.port.TransactionFileParser;
import com.cdq.banking.importing.domain.validation.TransactionValidator;
import com.cdq.banking.importing.domain.validation.ValidationResult;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvTransactionParser implements TransactionFileParser {

  private static final Set<String> REQUIRED_HEADERS =
      Set.of("iban", "date", "currency", "category", "amount");

  private final TransactionValidator validator;
  private final int maxRows;

  public CsvTransactionParser(TransactionValidator validator, int maxRows) {
    this.validator = validator;
    this.maxRows = maxRows;
  }

  @Override
  public ParseResult parse(byte[] data) {
    try (CSVReader reader = createReader(data)) {
      List<String> headers = readAndValidateHeaders(reader);
      return parseRows(reader, headers);
    } catch (FileParseException e) {
      throw e;
    } catch (Exception e) {
      throw new FileParseException("Failed to parse file", e);
    }
  }

  private CSVReader createReader(byte[] data) {
    return new CSVReaderBuilder(
            new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8))
        .build();
  }

  private List<String> readAndValidateHeaders(CSVReader reader) throws Exception {
    String[] headers = reader.readNext();
    if (headers == null) {
      throw new FileParseException("CSV file is empty — missing header row");
    }

    List<String> headerList =
        Arrays.stream(headers).map(String::trim).map(String::toLowerCase).toList();

    if (!new HashSet<>(headerList).containsAll(REQUIRED_HEADERS)) {
      throw new FileParseException("CSV missing required header columns");
    }

    return headerList;
  }

  private ParseResult parseRows(CSVReader reader, List<String> headers) throws Exception {
    List<Transaction> transactions = new ArrayList<>();
    List<RowError> rowErrors = new ArrayList<>();
    int totalRows = 0;

    String[] line;
    while ((line = reader.readNext()) != null) {
      totalRows++;
      if (totalRows > maxRows) {
        throw new FileParseException("CSV file exceeds maximum of " + maxRows + " rows");
      }

      Map<String, String> row = mapRow(headers, line);
      ValidationResult result = validator.validate(row);

      if (result.isValid()) {
        transactions.add(toTransaction(row));
      } else {
        rowErrors.add(new RowError(totalRows, result.errors()));
      }
    }

    return new ParseResult(transactions, totalRows, rowErrors);
  }

  private Map<String, String> mapRow(List<String> headers, String[] values) {
    Map<String, String> row = new HashMap<>();
    for (int i = 0; i < headers.size() && i < values.length; i++) {
      row.put(headers.get(i), values[i].trim());
    }
    return row;
  }

  private Transaction toTransaction(Map<String, String> row) {
    return Transaction.create(
        Iban.of(row.get("iban")),
        TransactionValidator.parseDate(row.get("date")),
        Money.of(new BigDecimal(row.get("amount")), Currency.getInstance(row.get("currency"))),
        TransactionCategory.valueOf(row.get("category")));
  }
}

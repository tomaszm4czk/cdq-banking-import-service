package com.cdq.banking.importing.adapter.persistence.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class TransactionDocument {

  @Id private String id;

  private String iban;

  private LocalDate date;

  private String currency;

  private String category;

  private BigDecimal amount;

  private String importJobId;
}

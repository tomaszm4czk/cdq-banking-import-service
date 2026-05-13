package com.cdq.banking.importing.domain.port;

import com.cdq.banking.importing.domain.model.Transaction;
import java.util.List;

public interface TransactionRepository {
  void saveAll(List<Transaction> transactions);

  void deleteByImportJobId(String importJobId);
}

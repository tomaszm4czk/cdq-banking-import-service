package com.cdq.banking.importing.adapter.persistence.repository;

import com.cdq.banking.importing.adapter.persistence.document.TransactionDocument;
import com.cdq.banking.importing.domain.model.Transaction;
import com.cdq.banking.importing.domain.port.TransactionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MongoTransactionRepository implements TransactionRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public void saveAll(List<Transaction> transactions) {
    if (transactions.isEmpty()) {
      return;
    }
    List<TransactionDocument> docs = transactions.stream().map(this::toDocument).toList();
    mongoTemplate.insertAll(docs);
  }

  @Override
  public void deleteByImportJobId(String importJobId) {
    mongoTemplate.remove(
        Query.query(Criteria.where("importJobId").is(importJobId)), TransactionDocument.class);
  }

  private TransactionDocument toDocument(Transaction tx) {
    return new TransactionDocument(
        tx.id(),
        tx.iban().value(),
        tx.date(),
        tx.money().currency().getCurrencyCode(),
        tx.category().name(),
        tx.money().amount(),
        tx.importJobId());
  }
}

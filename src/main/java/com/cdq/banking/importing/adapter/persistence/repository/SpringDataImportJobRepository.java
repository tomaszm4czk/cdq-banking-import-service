package com.cdq.banking.importing.adapter.persistence.repository;

import com.cdq.banking.importing.adapter.persistence.document.ImportJobDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

interface SpringDataImportJobRepository extends MongoRepository<ImportJobDocument, String> {
  List<ImportJobDocument> findByStatusIn(List<String> statuses);
}

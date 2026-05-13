package com.cdq.banking.importing.adapter.persistence.repository;

import com.cdq.banking.importing.adapter.persistence.document.ImportJobDocument;
import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.ImportStatus;
import com.cdq.banking.importing.domain.port.ImportJobRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MongoImportJobRepository implements ImportJobRepository {

  private final SpringDataImportJobRepository springRepo;

  @Override
  public void save(ImportJob job) {
    springRepo.save(ImportJobDocument.from(job));
  }

  @Override
  public Optional<ImportJob> findById(String id) {
    return springRepo.findById(id).map(ImportJobDocument::toDomain);
  }

  @Override
  public List<ImportJob> findByStatus(ImportStatus status) {
    return springRepo.findByStatusIn(List.of(status.name())).stream()
        .map(ImportJobDocument::toDomain)
        .toList();
  }
}

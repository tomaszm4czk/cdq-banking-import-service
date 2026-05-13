package com.cdq.banking.importing.domain.port;

import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.ImportStatus;
import java.util.List;
import java.util.Optional;

public interface ImportJobRepository {
  void save(ImportJob importJob);

  Optional<ImportJob> findById(String id);

  List<ImportJob> findByStatus(ImportStatus status);
}

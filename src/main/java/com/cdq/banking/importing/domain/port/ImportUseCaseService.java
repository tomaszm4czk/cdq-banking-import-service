package com.cdq.banking.importing.domain.port;

import com.cdq.banking.importing.domain.model.ImportJob;

public interface ImportUseCaseService {

  String importFile(String filename, byte[] content);

  ImportJob getStatus(String jobId);
}

package com.cdq.banking.importing.service;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.ParseResult;
import com.cdq.banking.importing.domain.port.ImportJobRepository;
import com.cdq.banking.importing.domain.port.ImportUseCaseService;
import com.cdq.banking.importing.domain.port.TransactionFileParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportTransactionsServiceImpl implements ImportUseCaseService {

  private final ImportJobRepository importJobRepository;
  private final ImportProcessor importProcessor;
  private final TransactionFileParserRegistry parserRegistry;

  @Override
  public String importFile(String filename, byte[] content) {
    if (filename == null || filename.isBlank()) {
      throw new DomainValidationException("Filename is required");
    }
    if (content == null || content.length == 0) {
      throw new DomainValidationException("File content is empty");
    }

    TransactionFileParser parser = parserRegistry.getParser(filename);
    ParseResult result = parser.parse(content);

    ImportJob job = ImportJob.create(filename);
    importJobRepository.save(job);
    log.info("Import job {} created for file '{}'", job.getId(), filename);

    importProcessor.process(job.getId(), result);
    return job.getId();
  }

  @Override
  public ImportJob getStatus(String jobId) {
    return importJobRepository
        .findById(jobId)
        .orElseThrow(
            () -> new com.cdq.banking.importing.domain.exception.ImportJobNotFoundException(jobId));
  }
}

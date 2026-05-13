package com.cdq.banking.importing.adapter.persistence.document;

import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.ImportStatus;
import com.cdq.banking.importing.domain.model.RowError;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "import_jobs")
public class ImportJobDocument {

  @Id private String id;
  private String filename;
  private Instant createdAt;
  private String status;
  private int totalRows;
  private int validRows;
  private int invalidRows;
  private List<RowError> rowErrors;
  private String errorMessage;

  public static ImportJobDocument from(ImportJob job) {
    return new ImportJobDocument(
        job.getId(),
        job.getFilename(),
        job.getCreatedAt(),
        job.getStatus().name(),
        job.getTotalRows(),
        job.getValidRows(),
        job.getInvalidRows(),
        job.getRowErrors(),
        job.getErrorMessage());
  }

  public ImportJob toDomain() {
    return ImportJob.reconstitute(
        id,
        filename,
        createdAt,
        ImportStatus.valueOf(status),
        totalRows,
        validRows,
        invalidRows,
        rowErrors,
        errorMessage);
  }
}

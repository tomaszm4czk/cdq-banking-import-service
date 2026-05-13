package com.cdq.banking.importing.adapter.api.dto;

import com.cdq.banking.importing.domain.model.ImportJob;
import com.cdq.banking.importing.domain.model.RowError;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Import job status with processing results")
public record ImportStatusResponse(
    @Schema(description = "Import job ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String jobId,
    @Schema(description = "Uploaded filename", example = "transactions.csv") String filename,
    @Schema(
            description = "Job status",
            example = "COMPLETED",
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "FAILED"})
        String status,
    @Schema(description = "Total number of rows in the file", example = "1000") int totalRows,
    @Schema(description = "Number of successfully imported rows", example = "990") int validRows,
    @Schema(description = "Number of rows with validation errors", example = "10") int invalidRows,
    @Schema(description = "Details of validation errors per row") List<RowError> rowErrors,
    @Schema(description = "Whether aggregation results are available", example = "true")
        boolean resultAvailable,
    @Schema(description = "Error message if import failed") String errorMessage) {
  public static ImportStatusResponse from(ImportJob job) {
    return new ImportStatusResponse(
        job.getId(),
        job.getFilename(),
        job.getStatus().name(),
        job.getTotalRows(),
        job.getValidRows(),
        job.getInvalidRows(),
        job.getRowErrors(),
        job.isResultAvailable(),
        job.getErrorMessage());
  }
}

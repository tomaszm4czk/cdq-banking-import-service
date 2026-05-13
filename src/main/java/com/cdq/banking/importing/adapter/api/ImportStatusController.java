package com.cdq.banking.importing.adapter.api;

import com.cdq.banking.importing.adapter.api.dto.ImportStatusResponse;
import com.cdq.banking.importing.domain.port.ImportUseCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
@Tag(name = "Import", description = "Transaction CSV import operations")
public class ImportStatusController {

  private final ImportUseCaseService importUseCase;

  @GetMapping("/{jobId}/status")
  @Operation(summary = "Check import job status and whether results are available")
  @ApiResponse(responseCode = "200", description = "Import job found")
  @ApiResponse(responseCode = "404", description = "Import job not found")
  public ImportStatusResponse getImportStatus(@PathVariable String jobId) {
    return ImportStatusResponse.from(importUseCase.getStatus(jobId));
  }
}

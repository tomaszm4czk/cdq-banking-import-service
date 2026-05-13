package com.cdq.banking.importing.adapter.api;

import com.cdq.banking.importing.adapter.api.dto.ImportResponse;
import com.cdq.banking.importing.domain.exception.FileParseException;
import com.cdq.banking.importing.domain.port.ImportUseCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
@Tag(name = "Import", description = "Transaction CSV import operations")
public class ImportController {

  private final ImportUseCaseService importUseCase;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Import transactions from file (async processing)")
  @ApiResponse(responseCode = "202", description = "Import accepted, poll status for results")
  @ApiResponse(responseCode = "400", description = "Invalid or empty file")
  public ImportResponse importFile(@RequestParam("file") MultipartFile file) {
    String jobId = importUseCase.importFile(file.getOriginalFilename(), readBytes(file));
    return new ImportResponse(
        jobId, "Import accepted, poll /api/v1/imports/" + jobId + "/status for results");
  }

  private byte[] readBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (IOException e) {
      throw new FileParseException("Failed to read uploaded file", e);
    }
  }
}

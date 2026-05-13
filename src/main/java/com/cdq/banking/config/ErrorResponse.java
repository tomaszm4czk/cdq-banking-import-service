package com.cdq.banking.config;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Standard error response")
public record ErrorResponse(
    @Schema(description = "Error type", example = "Bad Request") String error,
    @Schema(description = "Error details", example = "CSV missing required header columns")
        String message,
    @Schema(description = "Timestamp of the error") Instant timestamp) {
  public static ErrorResponse of(String error, String message) {
    return new ErrorResponse(error, message, Instant.now());
  }
}

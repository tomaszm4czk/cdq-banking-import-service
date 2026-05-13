package com.cdq.banking.importing.adapter.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after initiating a file import")
public record ImportResponse(
    @Schema(
            description = "Unique import job identifier",
            example = "550e8400-e29b-41d4-a716-446655440000")
        String jobId,
    @Schema(description = "Status message", example = "Import completed") String message) {}

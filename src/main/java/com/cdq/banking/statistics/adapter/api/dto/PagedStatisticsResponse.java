package com.cdq.banking.statistics.adapter.api.dto;

import com.cdq.banking.statistics.domain.model.PagedResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated statistics response")
public record PagedStatisticsResponse(
    @Schema(description = "Statistics entries for this page") List<StatisticsResponse> content,
    @Schema(description = "Current page number (0-based)", example = "0") int page,
    @Schema(description = "Page size", example = "20") int size,
    @Schema(description = "Total number of results", example = "142") long totalElements,
    @Schema(description = "Total number of pages", example = "8") int totalPages) {

  public static PagedStatisticsResponse from(PagedResult<StatisticsResponse> result) {
    return new PagedStatisticsResponse(
        result.content(),
        result.page(),
        result.size(),
        result.totalElements(),
        result.totalPages());
  }
}

package com.cdq.banking.statistics.adapter.api.dto;

import com.cdq.banking.statistics.domain.model.GroupBy;
import com.cdq.banking.statistics.domain.model.StatisticsFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.YearMonth;

@Schema(description = "Statistics search criteria")
public record StatisticsRequest(
    @Schema(description = "Grouping dimension", example = "CATEGORY") GroupBy groupBy,
    @Schema(description = "Filter by IBAN (optional)", example = "DE89370400440532013000")
        String iban,
    @Schema(description = "Start of month range (optional)", example = "2026-05") String fromMonth,
    @Schema(description = "End of month range (optional)", example = "2026-08") String toMonth,
    @Schema(description = "Page number (0-based)", example = "0") Integer page,
    @Schema(description = "Page size (1-100)", example = "20") Integer size) {

  public StatisticsFilter toFilter() {
    return new StatisticsFilter(
        groupBy,
        iban,
        fromMonth != null ? YearMonth.parse(fromMonth) : null,
        toMonth != null ? YearMonth.parse(toMonth) : null,
        page != null ? page : StatisticsFilter.DEFAULT_PAGE,
        size != null ? size : StatisticsFilter.DEFAULT_SIZE);
  }
}

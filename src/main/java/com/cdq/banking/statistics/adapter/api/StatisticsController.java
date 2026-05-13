package com.cdq.banking.statistics.adapter.api;

import com.cdq.banking.statistics.adapter.api.dto.PagedStatisticsResponse;
import com.cdq.banking.statistics.adapter.api.dto.StatisticsRequest;
import com.cdq.banking.statistics.adapter.api.dto.StatisticsResponse;
import com.cdq.banking.statistics.domain.model.PagedResult;
import com.cdq.banking.statistics.domain.model.StatisticsResult;
import com.cdq.banking.statistics.domain.port.GetStatisticsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Transaction statistics and aggregations")
public class StatisticsController {

  private final GetStatisticsUseCase statisticsUseCase;

  @PostMapping("/search")
  @Operation(summary = "Search transaction statistics with optional filters and pagination")
  @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
  public PagedStatisticsResponse searchStatistics(@RequestBody StatisticsRequest request) {
    PagedResult<StatisticsResult> result = statisticsUseCase.getStatistics(request.toFilter());
    return PagedStatisticsResponse.from(result.map(StatisticsResponse::from));
  }
}

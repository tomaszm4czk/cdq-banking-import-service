package com.cdq.banking.statistics.service;

import com.cdq.banking.statistics.domain.model.PagedResult;
import com.cdq.banking.statistics.domain.model.StatisticsFilter;
import com.cdq.banking.statistics.domain.model.StatisticsResult;
import com.cdq.banking.statistics.domain.port.GetStatisticsUseCase;
import com.cdq.banking.statistics.domain.port.StatisticsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService implements GetStatisticsUseCase {

  private final StatisticsQueryRepository queryRepository;

  @Override
  public PagedResult<StatisticsResult> getStatistics(StatisticsFilter filter) {
    return switch (filter.groupBy()) {
      case CATEGORY -> queryRepository.findByCategory(filter);
      case IBAN -> queryRepository.findByIban(filter);
      case MONTH -> queryRepository.findByMonth(filter);
    };
  }
}

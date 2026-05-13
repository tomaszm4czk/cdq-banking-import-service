package com.cdq.banking.statistics.domain.port;

import com.cdq.banking.statistics.domain.model.PagedResult;
import com.cdq.banking.statistics.domain.model.StatisticsFilter;
import com.cdq.banking.statistics.domain.model.StatisticsResult;

public interface StatisticsQueryRepository {
  PagedResult<StatisticsResult> findByCategory(StatisticsFilter filter);

  PagedResult<StatisticsResult> findByIban(StatisticsFilter filter);

  PagedResult<StatisticsResult> findByMonth(StatisticsFilter filter);
}

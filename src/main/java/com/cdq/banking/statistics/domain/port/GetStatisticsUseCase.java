package com.cdq.banking.statistics.domain.port;

import com.cdq.banking.statistics.domain.model.PagedResult;
import com.cdq.banking.statistics.domain.model.StatisticsFilter;
import com.cdq.banking.statistics.domain.model.StatisticsResult;

public interface GetStatisticsUseCase {

  PagedResult<StatisticsResult> getStatistics(StatisticsFilter filter);
}

package com.cdq.banking.statistics.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cdq.banking.statistics.domain.model.*;
import com.cdq.banking.statistics.domain.port.StatisticsQueryRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

  @Mock private StatisticsQueryRepository queryRepository;

  @InjectMocks private StatisticsService service;

  @Test
  void shouldReturnPagedCategoryStatistics() {
    var filter = new StatisticsFilter(GroupBy.CATEGORY, null, null, null, 0, 20);
    var stats =
        new CategoryStatistics(
            "GROCERIES",
            "EUR",
            5,
            new BigDecimal("250.00"),
            new BigDecimal("50.00"),
            new BigDecimal("10.00"),
            new BigDecimal("100.00"));
    when(queryRepository.findByCategory(filter))
        .thenReturn(PagedResult.of(List.of(stats), 0, 20, 1));

    PagedResult<StatisticsResult> result = service.getStatistics(filter);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst()).isInstanceOf(CategoryStatistics.class);
    var category = (CategoryStatistics) result.content().getFirst();
    assertThat(category.category()).isEqualTo("GROCERIES");
    assertThat(result.totalElements()).isEqualTo(1);
  }

  @Test
  void shouldReturnPagedIbanStatistics() {
    var filter = new StatisticsFilter(GroupBy.IBAN, null, null, null, 0, 20);
    var stats =
        new IbanStatistics(
            "DE89370400440532013000",
            "EUR",
            3,
            new BigDecimal("150.00"),
            new BigDecimal("50.00"),
            new BigDecimal("10.00"),
            new BigDecimal("100.00"));
    when(queryRepository.findByIban(filter)).thenReturn(PagedResult.of(List.of(stats), 0, 20, 1));

    PagedResult<StatisticsResult> result = service.getStatistics(filter);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst()).isInstanceOf(IbanStatistics.class);
  }

  @Test
  void shouldReturnPagedMonthlyStatistics() {
    var filter = new StatisticsFilter(GroupBy.MONTH, null, null, null, 0, 20);
    var stats =
        new MonthlyStatistics(
            2026,
            1,
            "EUR",
            10,
            new BigDecimal("5000.00"),
            new BigDecimal("500.00"),
            new BigDecimal("100.00"),
            new BigDecimal("1000.00"));
    when(queryRepository.findByMonth(filter)).thenReturn(PagedResult.of(List.of(stats), 0, 20, 1));

    PagedResult<StatisticsResult> result = service.getStatistics(filter);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst()).isInstanceOf(MonthlyStatistics.class);
  }
}

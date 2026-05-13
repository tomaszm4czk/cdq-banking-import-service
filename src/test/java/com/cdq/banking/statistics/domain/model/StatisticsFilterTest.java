package com.cdq.banking.statistics.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class StatisticsFilterTest {

  @Test
  void shouldCreateFilterWithAllFields() {
    var filter =
        new StatisticsFilter(
            GroupBy.CATEGORY,
            "DE89370400440532013000",
            YearMonth.of(2026, 5),
            YearMonth.of(2026, 8),
            0,
            20);

    assertThat(filter.groupBy()).isEqualTo(GroupBy.CATEGORY);
    assertThat(filter.iban()).isEqualTo("DE89370400440532013000");
    assertThat(filter.fromMonth()).isEqualTo(YearMonth.of(2026, 5));
    assertThat(filter.toMonth()).isEqualTo(YearMonth.of(2026, 8));
    assertThat(filter.page()).isZero();
    assertThat(filter.size()).isEqualTo(20);
  }

  @Test
  void shouldCreateFilterWithNoOptionalFields() {
    var filter = new StatisticsFilter(GroupBy.IBAN, null, null, null, 0, 20);

    assertThat(filter.iban()).isNull();
    assertThat(filter.fromMonth()).isNull();
    assertThat(filter.toMonth()).isNull();
  }

  @Test
  void shouldRejectNullGroupBy() {
    assertThatThrownBy(() -> new StatisticsFilter(null, null, null, null, 0, 20))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("groupBy");
  }

  @Test
  void shouldRejectFromMonthAfterToMonth() {
    assertThatThrownBy(
            () ->
                new StatisticsFilter(
                    GroupBy.CATEGORY, null, YearMonth.of(2026, 8), YearMonth.of(2026, 5), 0, 20))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("fromMonth must not be after toMonth");
  }

  @Test
  void shouldAllowOnlyFromMonth() {
    var filter = new StatisticsFilter(GroupBy.MONTH, null, YearMonth.of(2026, 5), null, 0, 20);
    assertThat(filter.fromMonth()).isEqualTo(YearMonth.of(2026, 5));
    assertThat(filter.toMonth()).isNull();
  }

  @Test
  void shouldAllowOnlyToMonth() {
    var filter = new StatisticsFilter(GroupBy.MONTH, null, null, YearMonth.of(2026, 8), 0, 20);
    assertThat(filter.toMonth()).isEqualTo(YearMonth.of(2026, 8));
  }

  @Test
  void shouldRejectNegativePage() {
    assertThatThrownBy(() -> new StatisticsFilter(GroupBy.CATEGORY, null, null, null, -1, 20))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("page");
  }

  @Test
  void shouldRejectSizeLessThanOne() {
    assertThatThrownBy(() -> new StatisticsFilter(GroupBy.CATEGORY, null, null, null, 0, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("size");
  }

  @Test
  void shouldRejectSizeGreaterThan100() {
    assertThatThrownBy(() -> new StatisticsFilter(GroupBy.CATEGORY, null, null, null, 0, 101))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("size");
  }
}

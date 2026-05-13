package com.cdq.banking.statistics.domain.model;

import java.time.YearMonth;

public record StatisticsFilter(
    GroupBy groupBy, String iban, YearMonth fromMonth, YearMonth toMonth, int page, int size) {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;
  public static final int MAX_SIZE = 100;

  public StatisticsFilter {
    if (groupBy == null) {
      throw new IllegalArgumentException("groupBy must not be null");
    }
    if (fromMonth != null && toMonth != null && fromMonth.isAfter(toMonth)) {
      throw new IllegalArgumentException("fromMonth must not be after toMonth");
    }
    if (page < 0) {
      throw new IllegalArgumentException("page must not be negative");
    }
    if (size < 1 || size > MAX_SIZE) {
      throw new IllegalArgumentException("size must be between 1 and " + MAX_SIZE);
    }
  }
}

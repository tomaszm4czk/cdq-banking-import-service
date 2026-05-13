package com.cdq.banking.statistics.domain.model;

import java.math.BigDecimal;

public record MonthlyStatistics(
    int year,
    int month,
    String currency,
    long transactionCount,
    BigDecimal totalAmount,
    BigDecimal averageAmount,
    BigDecimal minAmount,
    BigDecimal maxAmount)
    implements StatisticsResult {}

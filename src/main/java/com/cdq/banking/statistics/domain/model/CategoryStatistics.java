package com.cdq.banking.statistics.domain.model;

import java.math.BigDecimal;

public record CategoryStatistics(
    String category,
    String currency,
    long transactionCount,
    BigDecimal totalAmount,
    BigDecimal averageAmount,
    BigDecimal minAmount,
    BigDecimal maxAmount)
    implements StatisticsResult {}

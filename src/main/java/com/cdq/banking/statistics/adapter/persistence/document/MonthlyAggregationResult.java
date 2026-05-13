package com.cdq.banking.statistics.adapter.persistence.document;

import java.math.BigDecimal;

public record MonthlyAggregationResult(
    MonthlyGroupId id,
    long transactionCount,
    BigDecimal totalAmount,
    BigDecimal averageAmount,
    BigDecimal minAmount,
    BigDecimal maxAmount) {}

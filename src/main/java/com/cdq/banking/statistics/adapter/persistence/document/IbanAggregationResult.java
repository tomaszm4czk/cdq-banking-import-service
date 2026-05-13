package com.cdq.banking.statistics.adapter.persistence.document;

import java.math.BigDecimal;

public record IbanAggregationResult(
    IbanGroupId id,
    long transactionCount,
    BigDecimal totalAmount,
    BigDecimal averageAmount,
    BigDecimal minAmount,
    BigDecimal maxAmount) {}

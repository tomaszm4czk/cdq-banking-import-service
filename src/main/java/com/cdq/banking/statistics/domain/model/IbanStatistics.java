package com.cdq.banking.statistics.domain.model;

import java.math.BigDecimal;

public record IbanStatistics(
    String iban,
    String currency,
    long transactionCount,
    BigDecimal totalAmount,
    BigDecimal averageAmount,
    BigDecimal minAmount,
    BigDecimal maxAmount)
    implements StatisticsResult {}

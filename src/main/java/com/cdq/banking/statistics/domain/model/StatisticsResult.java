package com.cdq.banking.statistics.domain.model;

public sealed interface StatisticsResult
    permits CategoryStatistics, IbanStatistics, MonthlyStatistics {}

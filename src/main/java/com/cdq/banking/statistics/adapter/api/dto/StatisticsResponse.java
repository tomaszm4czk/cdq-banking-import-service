package com.cdq.banking.statistics.adapter.api.dto;

import com.cdq.banking.statistics.domain.model.CategoryStatistics;
import com.cdq.banking.statistics.domain.model.IbanStatistics;
import com.cdq.banking.statistics.domain.model.MonthlyStatistics;
import com.cdq.banking.statistics.domain.model.StatisticsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Transaction statistics entry")
public record StatisticsResponse(
    @Schema(description = "Group key (category name, IBAN, or year-month)", example = "GROCERIES")
        String group,
    @Schema(description = "Currency code", example = "EUR") String currency,
    @Schema(description = "Number of transactions", example = "15") long transactionCount,
    @Schema(description = "Sum of all transaction amounts", example = "450.99")
        BigDecimal totalAmount,
    @Schema(description = "Average transaction amount", example = "30.07") BigDecimal averageAmount,
    @Schema(description = "Smallest transaction amount", example = "5.50") BigDecimal minAmount,
    @Schema(description = "Largest transaction amount", example = "120.00") BigDecimal maxAmount) {

  public static StatisticsResponse from(StatisticsResult result) {
    return switch (result) {
      case CategoryStatistics s -> from(s);
      case IbanStatistics s -> from(s);
      case MonthlyStatistics s -> from(s);
    };
  }

  public static StatisticsResponse from(CategoryStatistics s) {
    return new StatisticsResponse(
        s.category(),
        s.currency(),
        s.transactionCount(),
        s.totalAmount(),
        s.averageAmount(),
        s.minAmount(),
        s.maxAmount());
  }

  public static StatisticsResponse from(IbanStatistics s) {
    return new StatisticsResponse(
        s.iban(),
        s.currency(),
        s.transactionCount(),
        s.totalAmount(),
        s.averageAmount(),
        s.minAmount(),
        s.maxAmount());
  }

  public static StatisticsResponse from(MonthlyStatistics s) {
    return new StatisticsResponse(
        s.year() + "-" + String.format("%02d", s.month()),
        s.currency(),
        s.transactionCount(),
        s.totalAmount(),
        s.averageAmount(),
        s.minAmount(),
        s.maxAmount());
  }
}

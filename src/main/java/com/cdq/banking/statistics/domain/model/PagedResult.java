package com.cdq.banking.statistics.domain.model;

import java.util.List;
import java.util.function.Function;

public record PagedResult<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public PagedResult {
    if (content == null) {
      content = List.of();
    }
  }

  public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements) {
    int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    return new PagedResult<>(content, page, size, totalElements, totalPages);
  }

  public <R> PagedResult<R> map(Function<T, R> mapper) {
    return new PagedResult<>(
        content.stream().map(mapper).toList(), page, size, totalElements, totalPages);
  }
}

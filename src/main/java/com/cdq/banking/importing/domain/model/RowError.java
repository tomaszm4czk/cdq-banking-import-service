package com.cdq.banking.importing.domain.model;

import java.util.List;

public record RowError(int row, List<String> errors) {}

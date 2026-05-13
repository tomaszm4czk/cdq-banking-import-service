package com.cdq.banking.importing.domain.port;

import com.cdq.banking.importing.domain.model.ParseResult;

public interface TransactionFileParser {

  ParseResult parse(byte[] data);
}

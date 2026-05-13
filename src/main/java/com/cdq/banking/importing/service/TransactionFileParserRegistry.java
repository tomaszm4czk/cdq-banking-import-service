package com.cdq.banking.importing.service;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import com.cdq.banking.importing.domain.port.TransactionFileParser;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.util.StringUtils;

public class TransactionFileParserRegistry {

  private final Map<String, TransactionFileParser> parsers;

  public TransactionFileParserRegistry(Map<String, TransactionFileParser> parsers) {
    this.parsers = Map.copyOf(parsers);
  }

  public TransactionFileParser getParser(String filename) {
    String extension =
        Optional.ofNullable(StringUtils.getFilenameExtension(filename))
            .map(String::toLowerCase)
            .orElseThrow(() -> new DomainValidationException("File has no extension"));

    return Optional.ofNullable(parsers.get(extension))
        .orElseThrow(
            () ->
                new DomainValidationException(
                    "Unsupported file format: "
                        + extension
                        + ". Supported: "
                        + supportedExtensions()));
  }

  public Set<String> supportedExtensions() {
    return parsers.keySet();
  }
}

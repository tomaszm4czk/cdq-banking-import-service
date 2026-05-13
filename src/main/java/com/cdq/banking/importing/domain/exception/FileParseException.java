package com.cdq.banking.importing.domain.exception;

public class FileParseException extends RuntimeException {

  public FileParseException(String message) {
    super(message);
  }

  public FileParseException(String message, Throwable cause) {
    super(message, cause);
  }
}

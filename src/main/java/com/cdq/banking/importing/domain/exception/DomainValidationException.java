package com.cdq.banking.importing.domain.exception;

public class DomainValidationException extends RuntimeException {

  public DomainValidationException(String message) {
    super(message);
  }
}

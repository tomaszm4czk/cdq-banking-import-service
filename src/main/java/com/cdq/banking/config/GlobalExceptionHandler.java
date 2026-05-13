package com.cdq.banking.config;

import com.cdq.banking.importing.domain.exception.DomainValidationException;
import com.cdq.banking.importing.domain.exception.FileParseException;
import com.cdq.banking.importing.domain.exception.ImportJobNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ErrorResponse> handleDomainValidation(DomainValidationException e) {
    return ResponseEntity.badRequest().body(ErrorResponse.of("Validation Error", e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(ErrorResponse.of("Validation Error", e.getMessage()));
  }

  @ExceptionHandler(ImportJobNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ImportJobNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of("Not Found", e.getMessage()));
  }

  @ExceptionHandler(FileParseException.class)
  public ResponseEntity<ErrorResponse> handleFileParse(FileParseException e) {
    return ResponseEntity.unprocessableEntity()
        .body(ErrorResponse.of("File Parse Error", e.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException e) {
    return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
        .body(ErrorResponse.of("File Too Large", "Maximum file size exceeded"));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.of("Bad Request", "Malformed or unreadable request body"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
    log.error("Unexpected error", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of("Internal Server Error", "An unexpected error occurred"));
  }
}

package com.smoothy.authentication.infrastructure.Exceptions;

import org.springframework.validation.Errors;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

  public Errors getBindingResult() {
      return getBindingResult();
  }
}

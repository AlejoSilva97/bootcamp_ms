package com.example.bootcamp.domain.exceptions;

public class BootcampAlreadyExistsException extends RuntimeException {
    public BootcampAlreadyExistsException(String message) {
        super(message);
    }
}

package com.example.bootcamp.domain.exceptions;

public class BootcampNotFoundException extends RuntimeException {
    public BootcampNotFoundException(String message) {
        super(message);
    }
}

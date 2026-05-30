package com.example.bootcamp.domain.exceptions;

public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException(String message) {
        super(message);
    }
}

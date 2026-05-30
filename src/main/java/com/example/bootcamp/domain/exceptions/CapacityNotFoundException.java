package com.example.bootcamp.domain.exceptions;

public class CapacityNotFoundException extends RuntimeException{
    public CapacityNotFoundException(String message){
        super(message);
    }
}

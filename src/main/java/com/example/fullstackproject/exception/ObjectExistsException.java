package com.example.fullstackproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ObjectExistsException extends RuntimeException{

    public ObjectExistsException(String message) {
        super(message);
    }
}

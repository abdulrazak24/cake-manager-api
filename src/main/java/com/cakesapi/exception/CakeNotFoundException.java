package com.cakesapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CakeNotFoundException extends ResponseStatusException {

    public CakeNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

package com.so5.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {

    public EntityNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Not found.");
    }
}

package com.so5.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoCreditCardDataException extends ResponseStatusException {

    public NoCreditCardDataException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "No credit card data found.");
    }
}

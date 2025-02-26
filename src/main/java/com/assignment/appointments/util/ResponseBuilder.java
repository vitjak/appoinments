package com.assignment.appointments.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {

    private final HttpHeaders headers = new HttpHeaders();
    private HttpStatus status = HttpStatus.OK;
    private Object body;

    public static ResponseBuilder create() {
        return new ResponseBuilder();
    }

    public ResponseBuilder status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ResponseBuilder header(String key, String value) {
        this.headers.add(key, value);
        return this;
    }

    public ResponseBuilder entity(Object body) {
        this.body = body;
        return this;
    }

    public ResponseEntity<Object> build() {
        return new ResponseEntity<>(body, headers, status);
    }
}
package com.paulovargas.erpapi.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class ApiError {

    private final LocalDateTime timestamp;

    private final int status;

    private final String error;

    private final String message;

    private final String path;

    private final Map<String, String> fields;

    public ApiError(int status, String error, String message, String path, Map<String, String> fields) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fields = fields;
    }
}


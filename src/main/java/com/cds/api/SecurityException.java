package com.cds.api;

import org.springframework.http.HttpStatus;

public class SecurityException extends RuntimeException {
    private static final long serialVersionUID = -7806029002430564887L;

    private String message;
    private HttpStatus status;

    public SecurityException() {
    }

    public SecurityException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}

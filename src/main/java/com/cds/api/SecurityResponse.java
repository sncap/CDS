package com.cds.api;

import org.springframework.http.HttpStatus;

public class SecurityResponse {
    private String error;
    private HttpStatus status;

    public SecurityResponse() {

    }

    public SecurityResponse(HttpStatus status, String error) {
        this.status = status;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}

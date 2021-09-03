package com.revature.deltaforce.web.dtos;

import java.time.LocalDateTime;

public class ErrorResponse {

    private int statusCode;
    private String message;
    private String timestamp;

    public ErrorResponse() {
        super();
    }

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }
}

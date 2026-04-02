package com.assignment.github_access_report.Exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final int status;
    private final String errorCode;

    public ApiException(String message, int status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}

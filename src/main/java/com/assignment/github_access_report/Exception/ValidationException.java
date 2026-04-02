package com.assignment.github_access_report.Exception;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(message, 400, "VALIDATION_ERROR");
    }
}

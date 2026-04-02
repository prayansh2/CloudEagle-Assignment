package com.assignment.github_access_report.Exception.Handler;

import com.assignment.github_access_report.Exception.ApiException;
import com.assignment.github_access_report.model.Response.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(ex.getStatus())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(500)
                .message(ex.getMessage())
                .errorCode("Server Error")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(500).body(response);
    }
}

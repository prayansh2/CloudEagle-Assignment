package com.assignment.github_access_report.model.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    private int status;
    private String message;
    private String errorCode;
    private Instant timestamp;
}

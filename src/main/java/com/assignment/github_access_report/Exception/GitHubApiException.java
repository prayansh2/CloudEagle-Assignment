package com.assignment.github_access_report.Exception;

public class GitHubApiException extends ApiException {
    public GitHubApiException(String message, int status) {
        super(message, status, "GITHUB_ERROR");
    }
}

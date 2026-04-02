package com.assignment.github_access_report.utils;

import com.assignment.github_access_report.Exception.ValidationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class Helper {


    public static boolean isValidOrgName(String name) {
        if (name == null) return false;

        int length = name.length();

        if (length < 1 || length > 39) return false;

        String regex = "^[a-zA-Z0-9](?:-?[a-zA-Z0-9])*$";

        return name.matches(regex);
    }

    public static HttpEntity<Void> createHeaders(String githubToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }

    public static void validateToken(String PAT) {

        if (PAT == null) {
            throw new ValidationException("Token is missing");
        }


        if (PAT.isBlank()) {
            throw new ValidationException("Token is invalid (empty or blank)");
        }

        if (PAT.length() < 20) {
            throw new ValidationException("Token is too short");
        }

        if (!PAT.matches("^(ghp_).+")) {
            throw new ValidationException("Invalid GitHub token format");
        }
    }



}

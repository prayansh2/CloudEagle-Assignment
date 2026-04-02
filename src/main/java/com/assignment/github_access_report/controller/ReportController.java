package com.assignment.github_access_report.controller;


import com.assignment.github_access_report.model.Response.ApiResponse;
import com.assignment.github_access_report.model.Repository;
import com.assignment.github_access_report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Report Controller", description = "APIs for Github Organization Repositories with Collaborators")
@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/generate-report")
    public ApiResponse<List<Repository>> getReport(@RequestParam String organizationName,@RequestHeader("X-GitHub-Token") String token) {
        List<Repository> data =service.generateReport(organizationName,token);
        return ApiResponse.<List<Repository>>builder()
                        .status("SUCCESS")
                        .message("Report generated successfully")
                        .data(data)
                        .build();
    }
}



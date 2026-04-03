package com.assignment.github_access_report.service;

import com.assignment.github_access_report.Exception.ValidationException;
import com.assignment.github_access_report.client.GithubClient;
import com.assignment.github_access_report.model.Repository;
import com.assignment.github_access_report.utils.Helper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final GithubClient client;

    public ReportService(GithubClient client) {
        this.client = client;
    }

    public List<Repository> generateReport(String org,String PAT) {
        List<Repository> result ;
        if(!Helper.isValidOrgName(org))
            throw new ValidationException("Organization name not valid");
        Helper.validateToken(PAT);

          result =  this.client.getRepoWithCollaborators(org,PAT);


        return result;
    }
}


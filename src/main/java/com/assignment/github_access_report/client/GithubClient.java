package com.assignment.github_access_report.client;

import com.assignment.github_access_report.Exception.GitHubApiException;
import com.assignment.github_access_report.model.Collaborator;
import com.assignment.github_access_report.model.Repository;
import com.assignment.github_access_report.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
public class GithubClient {

    private static final int PAGE_SIZE = 100;
    private final RestTemplate restTemplate;
    private final String REPOSITORY_URL ;
    private final String REPOSITORY_COLLABORATOR_URL;
    public GithubClient(@Value("${github.REPOSITORY_URL}") String REPOSITORY_URL,
                        @Value("${github.REPOSITORY_COLLABORATOR_URL}") String REPOSITORY_COLLABORATOR_URL,
                        RestTemplate restTemplate) {
        this.REPOSITORY_URL=REPOSITORY_URL;
        this.REPOSITORY_COLLABORATOR_URL=REPOSITORY_COLLABORATOR_URL;
        this.restTemplate=restTemplate;
    }



public List<Repository> getRepoWithCollaborators(String orgName, String PAT_TOKEN) {

    HttpEntity<Void> entity = Helper.createHeaders(PAT_TOKEN);

    List<Repository> allRepos = this.fetchAllRepositories(orgName ,entity);


    List<CompletableFuture<Void>> futures = allRepos.stream()
            .map(repo -> CompletableFuture.runAsync(() -> {
                try {
                    List<Collaborator> collaborators =
                            fetchCollaborators(orgName, repo.getName(), entity);

                    repo.setCollaborators(collaborators);

                } catch (Exception e) {
                    log.error("Failed to fetch collaborators for repo: {}", repo.getName(), e);
                }
            }))
            .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    return allRepos;
}

public List<Repository> fetchAllRepositories(String orgName ,HttpEntity<Void> entity) {

   log.info("Fetching all repositories for org: {} ", orgName);

    List<Repository> all = new ArrayList<>();
    int page = 1;


    while (true) {
        List<Repository> batch = fetchRepositoriesPage(orgName, page, entity);

        if (batch.isEmpty()) break;

        all.addAll(batch);

        if (batch.size() < PAGE_SIZE) break;

        page++;
    }

    log.info("Total repositories for org  {} : {}",orgName,all.size());
    return all;
}

    public List<Repository> fetchRepositoriesPage(
            String orgName,
            int page,
            HttpEntity<Void> entity) {

        try {
            ResponseEntity<Repository[]> response = restTemplate.exchange(
                    this.REPOSITORY_URL,
                    HttpMethod.GET,
                    entity,
                    Repository[].class,
                    orgName,
                    PAGE_SIZE,
                    page
            );

            Repository[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();

        } catch (HttpClientErrorException ex) {

            int status = ex.getStatusCode().value();

            log.error("Error occur while Fetching Repository for Organization: {} for Page: {}",orgName,page,ex);
            if (status == 401) {
                log.error("Github Token is Invalid");
                throw new GitHubApiException("Invalid GitHub token", 401);
            }

            if (status == 403) {
                throw new GitHubApiException("Rate limit exceeded", 403);
            }

            if (status == 404) {
                log.error("No Organization Found with Name: {}",orgName);
                throw new GitHubApiException("Organization not found with Name: " + orgName, 404);
            }

            throw new GitHubApiException("Error in Github Api " + ex.getMessage(), status);

        }
//        catch (HttpServerErrorException ex) {
//
//            throw new GitHubApiException("GitHub server error", ex.getStatusCode().value());
//
//        }
        catch (Exception ex) {
            throw new GitHubApiException("Unexpected error: " + ex.getMessage(), 500);
        }
    }


public List<Collaborator> fetchCollaborators(
        String orgName,
        String repoName,
        HttpEntity<Void> entity) {

    List<Collaborator> all = new ArrayList<>();
    int page = 1;

    while (true) {
        List<Collaborator> batch =
                fetchCollaboratorsPage(orgName, repoName, page, entity);

        if (batch.isEmpty()) break;

        all.addAll(batch);

        if (batch.size() < PAGE_SIZE) break;

        page++;
    }

    return all;
}

    public List<Collaborator> fetchCollaboratorsPage(
            String orgName,
            String repoName,
            int page,
            HttpEntity<Void> entity) {

        try {
            ResponseEntity<Collaborator[]> response = restTemplate.exchange(
                    this.REPOSITORY_COLLABORATOR_URL,
                    HttpMethod.GET,
                    entity,
                    Collaborator[].class,
                    orgName,
                    repoName,
                    PAGE_SIZE,
                    page
            );

            Collaborator[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();

        } catch (HttpClientErrorException ex) {

            int status = ex.getStatusCode().value();
            log.error(
                    "Error while fetching collaborators for repo: {} org: {} page: {}",
                    repoName, orgName, page, ex
            );
            if (status == 401) {
                log.error("Github Token is Invalid");
                throw new GitHubApiException("Invalid GitHub token", 401);
            }

            if (status == 403) {
                throw new GitHubApiException("Rate limit exceeded", 403);
            }

            if (status == 404) {
                log.error("No Organization Found with Name: {}",orgName);
                throw new GitHubApiException(
                        "Repository not found for Organization: " + orgName + " Repository:" + repoName,
                        404
                );
            }

            throw new GitHubApiException("Client error: " + ex.getMessage(), status);

        }
//        catch (HttpServerErrorException ex) {
//
//            throw new GitHubApiException("GitHub server error", ex.getStatusCode().value());
//
//        }
        catch (Exception ex) {

            throw new GitHubApiException("Unexpected error: " + ex.getMessage(), 500);
        }
    }



}
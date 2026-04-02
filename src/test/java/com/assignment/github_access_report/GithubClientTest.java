package com.assignment.github_access_report;

import com.assignment.github_access_report.Exception.GitHubApiException;
import com.assignment.github_access_report.client.GithubClient;
import com.assignment.github_access_report.model.Collaborator;
import com.assignment.github_access_report.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GithubClientTest {

    @Mock
    RestTemplate restTemplate;

    GithubClient githubClient;

    @Value("${github.uri}")
    private String githubBaseUrl;

    @Value("${github.REPOSITORY_URL}")
    private String githubRepositoryUrl;

    @Value("${github.REPOSITORY_COLLABORATOR_URL}")
    private String githubRepositoryCollaboratorsUrl;

    private HttpEntity<Void> entity;

    @BeforeEach
    void setUp() {
        entity = new HttpEntity<>(new HttpHeaders());
    }

    @BeforeEach
    void setup() {
        githubClient = new GithubClient(
                "https://api.github.com/orgs/{org}/repos?per_page={perPage}&page={page}",
                "https://api.github.com/repos/{org}/{repo}/collaborators?per_page={size}&page={page}",
                restTemplate
        );
    }

    @Test
    void shouldReturnRepositories_whenApiCallSuccessful() {
        Repository[] repos = {
                new Repository("repo1", null),
                new Repository("repo1", null),
        };

        ResponseEntity<Repository[]> response =
                new ResponseEntity<>(repos, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.github.com/orgs/{org}/repos?per_page={perPage}&page={page}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class),
                eq("test-org"),
                eq(100),
                eq(1)
        )).thenReturn(response);

        List<Repository> result =
                githubClient.fetchRepositoriesPage("test-org", 1, entity);

        assertEquals(2, result.size());
    }


    @Test
    void shouldThrowException_whenInvalidToken() {

        HttpClientErrorException exception =
                HttpClientErrorException.create(
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        HttpHeaders.EMPTY,
                        null,
                        null
                );

        when(restTemplate.exchange(
                eq("https://api.github.com/orgs/{org}/repos?per_page={perPage}&page={page}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class),
                eq("test-org"),
                eq(100),
                eq(1)
        )).thenThrow(exception);

        GitHubApiException thrown = assertThrows(
                GitHubApiException.class,
                () -> githubClient.fetchRepositoriesPage(
                        "test-org",
                        1,
                        new HttpEntity<>(new HttpHeaders())
                )
        );

        assertEquals(401, thrown.getStatus());
        assertEquals("Invalid GitHub token", thrown.getMessage());
    }

    @Test
    void shouldThrowException_whenOrgnizationNotExist() {

        HttpClientErrorException exception =
                HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "NOT_FOUND",
                        HttpHeaders.EMPTY,
                        null,
                        null
                );

        when(restTemplate.exchange(
                eq("https://api.github.com/orgs/{org}/repos?per_page={perPage}&page={page}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class),
                eq("test-org"),
                eq(100),
                eq(1)
        )).thenThrow(exception);

        GitHubApiException thrown = assertThrows(
                GitHubApiException.class,
                () -> githubClient.fetchRepositoriesPage(
                        "test-org",
                        1,
                        new HttpEntity<>(new HttpHeaders())
                )
        );

        assertEquals(404, thrown.getStatus());
        assertEquals("Organization not found with Name: " + "test-org", thrown.getMessage());
    }




@Test
void shouldReturnRepositoriesWithCollaborators_whenApiCallSuccessful() {
    Collaborator[] repos = {
            Collaborator.builder().username("user1").roleName("Admin").build(),
                    Collaborator.builder().username("user2").roleName("Read").build(),
            Collaborator.builder().username("user3").roleName("Maintain").build()
    };

    ResponseEntity<Collaborator[]> response =
            new ResponseEntity<>(repos, HttpStatus.OK);

    when(restTemplate.exchange(
            eq("https://api.github.com/repos/{org}/{repo}/collaborators?per_page={size}&page={page}"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Collaborator[].class),
            eq("test-org"),
            eq("repo1"),
            eq(100),
            eq(1)
    )).thenReturn(response);

    List<Collaborator> result =
            githubClient.fetchCollaboratorsPage("test-org", "repo1", 1,entity);

    assertEquals(3, result.size());
}

}

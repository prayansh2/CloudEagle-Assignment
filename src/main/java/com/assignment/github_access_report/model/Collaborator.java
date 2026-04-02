package com.assignment.github_access_report.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Collaborator {

//    private Long id;

    @JsonProperty("login")
    private String username;

//    @JsonProperty("avatar_url")
//    private String avatarUrl;
//
//    @JsonProperty("html_url")
//    private String htmlUrl;
//
//    private String type;

    @JsonProperty("role_name")
    private String roleName;


}


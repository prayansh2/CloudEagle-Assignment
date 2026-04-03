package com.assignment.github_access_report.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Collaborator {

    @JsonProperty("login")
    private String username;

    @JsonProperty("role_name")
    private String roleName;



}


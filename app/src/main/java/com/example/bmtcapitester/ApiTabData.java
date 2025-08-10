package com.example.bmtcapitester;

import java.io.Serializable;
import java.util.List;

public class ApiTabData implements Serializable {
    private String title;
    private String endpoint;
    private List<ApiParameter> parameters;

    public ApiTabData(String title, String endpoint, List<ApiParameter> parameters) {
        this.title = title;
        this.endpoint = endpoint;
        this.parameters = parameters;
    }

    // Getters
    public String getTitle() { return title; }
    public String getEndpoint() { return endpoint; }
    public List<ApiParameter> getParameters() { return parameters; }
}

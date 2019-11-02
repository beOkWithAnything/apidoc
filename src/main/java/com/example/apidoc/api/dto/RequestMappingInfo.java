package com.example.apidoc.api.dto;

import lombok.Data;

@Data
public class RequestMappingInfo {

    private String method;
    private String uri;

    public RequestMappingInfo(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }
}

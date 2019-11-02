package com.example.apidoc.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class MethodInfo implements Comparable<MethodInfo> {

    private String name;
    private String uri;
    private int order;
    private String method;
    private String methodName;
    private List<String> jsonPara;
    private List<ParameterInfo> parameterInfoList;

    @Override
    public int compareTo(MethodInfo o) {
        if (this.getOrder() > o.getOrder()) {
            return 1;
        } else {
            return -1;
        }
    }
}

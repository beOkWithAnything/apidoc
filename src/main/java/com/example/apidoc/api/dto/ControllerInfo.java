package com.example.apidoc.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ControllerInfo implements Comparable<ControllerInfo> {

    private String name;
    private String clazz;
    private int order;
    private List<MethodInfo> methodInfoList;

    @Override
    public int compareTo(ControllerInfo o) {
        if (this.getOrder() > o.getOrder()) {
            return 1;
        } else {
            return -1;
        }
    }
}

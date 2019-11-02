package com.example.apidoc.api.core;

import com.example.apidoc.api.dto.ControllerInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ControllerContext {

    private static final List<ControllerInfo> controllerList = new ArrayList<>();

    public List<ControllerInfo> getControllerList() {
        return controllerList;
    }

    public void add(ControllerInfo controllerInfo) {
        controllerList.add(controllerInfo);
    }
}

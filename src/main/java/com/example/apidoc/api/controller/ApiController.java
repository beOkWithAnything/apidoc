package com.example.apidoc.api.controller;

import com.example.apidoc.api.config.ApiConfig;
import com.example.apidoc.api.core.ControllerContext;
import com.example.apidoc.api.dto.ControllerInfo;
import com.example.apidoc.api.dto.MethodInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ControllerContext controllerContext;

    @Autowired
    private ApiConfig apiConfig;

    @GetMapping
    public String getApiDoc(Model model) {

        // url
        String url = apiConfig.getUrl();
        model.addAttribute("url", url);

        // projectName
        String projectName = apiConfig.getProjectName();
        model.addAttribute("projectName", projectName);

        List<ControllerInfo> controllerList = controllerContext.getControllerList();
        // 排序
        Collections.sort(controllerList);
        model.addAttribute("controllerList", controllerList);

        return "/zeguo";
    }

    @ResponseBody
    @GetMapping("/{clazz}/{methodName}")
    public MethodInfo getApi(@PathVariable("clazz") String clazz, @PathVariable("methodName") String methodName) {
        List<ControllerInfo> controllerList = controllerContext.getControllerList();
        for (ControllerInfo controllerInfo : controllerList) {
            if (controllerInfo.getClazz().equals(clazz)) {
                for (MethodInfo methodInfo : controllerInfo.getMethodInfoList()) {
                    if (methodInfo.getMethodName().equals(methodName)) {
                        return methodInfo;
                    }
                }
            }
        }
        return controllerList.get(0).getMethodInfoList().get(0);
    }
}

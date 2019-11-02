package com.example.apidoc.api.core;

import com.example.apidoc.api.annotation.ApiController;
import com.example.apidoc.api.annotation.ApiMethod;
import com.example.apidoc.api.annotation.ApiParam;
import com.example.apidoc.api.dto.ControllerInfo;
import com.example.apidoc.api.dto.MethodInfo;
import com.example.apidoc.api.dto.ParameterInfo;
import com.example.apidoc.api.dto.RequestMappingInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class AnnotationScan implements BeanPostProcessor {

    @Autowired
    private ControllerContext controllerContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        ControllerInfo controllerInfo = collectionMessage(bean, beanName);
        if (controllerInfo != null) {
//            System.out.println(controllerInfo.toString());
            controllerContext.add(controllerInfo);
        }
        return bean;
    }

    /**
     * 在创建 bean 的时候收集到在线文档需要的信息
     */
    private ControllerInfo collectionMessage(Object bean, String beanName) {

        /**
         * 检查是否存在 @SwqController 注解
         */
        Class<?> beanClass = ClassUtils.isCglibProxy(bean) ? bean.getClass().getSuperclass() : bean.getClass();
        RestController controller = AnnotationUtils.findAnnotation(beanClass, RestController.class);
        if (controller == null) {
            // 正在初始化的bean不是Controller
            return null;
        }
        ApiController apiController = beanClass.getDeclaredAnnotation(ApiController.class);
        if (apiController == null) {
            // 没有 @SwqController注解
            return null;
        }
        RequestMapping classMapping = beanClass.getAnnotation(RequestMapping.class);
        log.info("Find a ApiController [{}]", classMapping.value());

        /**
         * 获取 Controller 信息
         */
        ControllerInfo controllerInfo = new ControllerInfo();

        // 设置 Controller Class
        controllerInfo.setClazz(beanClass.getSimpleName());

        // 设置 Controller Name
        String name = apiController.value();
        if (name != null && !name.equals("")) {
            controllerInfo.setName(name);
        } else {
            controllerInfo.setName(beanName);
        }

        // 设置 Controller Order
        controllerInfo.setOrder(apiController.order());


        // 设置 Controller MethodInfos
        List<MethodInfo> methodInfoList = new ArrayList<>();
        Method[] methods = beanClass.getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return null;
        }
        for (Method method : methods) {
            RequestMappingInfo requestMappingInfo = findSwqMethod(method);
            if (requestMappingInfo == null) {
                // 方法上没有 @RequestMapping注解
                continue;
            }
            ApiMethod apiMethod = method.getAnnotation(ApiMethod.class);
            if (apiMethod == null) {
                // 方法上没有 @SwqMethod注解
                continue;
            }

            /**
             * 获取 Method信息
             */
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setName(apiMethod.value());
            methodInfo.setOrder(apiMethod.order());
            // 要么是 name， 要么就是 value[0]
            methodInfo.setUri(classMapping.value()[0] + requestMappingInfo.getUri());
            methodInfo.setMethod(requestMappingInfo.getMethod());
            methodInfo.setMethodName(method.getName());

            /**
             * 获取 params信息
             */
            Parameter[] parameters = method.getParameters();
            if (parameters != null && parameters.length > 0) {
                // 方法带参数
                for (Parameter parameter : parameters) {
                    Class<?> parameterClass = parameter.getType();
                    RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                    if (requestBody != null) {
                        // 如果带有 @RequestBody
                        Field[] fields = parameterClass.getDeclaredFields();
                        List<ParameterInfo> parameterInfoList = new ArrayList<>();
                        List<String> jsonParaList = new ArrayList<>();
                        for (int i = 0; i < fields.length; i++) {
                            String jsonPara = "";
                            Field field = fields[i];
                            ApiParam apiParam = field.getAnnotation(ApiParam.class);
                            if (apiParam != null) {
                                ParameterInfo parameterInfo = new ParameterInfo();
                                parameterInfo.setName(field.getName());
                                parameterInfo.setClazz(field.getType().getSimpleName());
                                parameterInfo.setNeed(apiParam.need() == 1 ? "是" : "否");
                                parameterInfo.setDescription(apiParam.description());
                                parameterInfo.setDemo(apiParam.demo());
                                parameterInfoList.add(parameterInfo);
                                jsonPara += "\"" + field.getName() + "\" : ";
                                if (field.getType().getSimpleName().equals("String")) {
                                    jsonPara += "\"" + apiParam.demo() + "\"";
                                } else {
                                    jsonPara += apiParam.demo();
                                }
                                if (i != fields.length - 1) {
                                    jsonPara += ",";
                                }
                                jsonParaList.add(jsonPara);
                            }
                        }
                        methodInfo.setJsonPara(jsonParaList);
                        methodInfo.setParameterInfoList(parameterInfoList);
                    }
                }
            } else {
                // 方法没有参数
                methodInfo.setParameterInfoList(null);
            }
            methodInfoList.add(methodInfo);
            Collections.sort(methodInfoList);
            controllerInfo.setMethodInfoList(methodInfoList);
            log.info("Find a ApiMethod [{}", requestMappingInfo.getUri());
        }
        return controllerInfo;
    }

    private RequestMappingInfo findSwqMethod(Method method) {

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return new RequestMappingInfo("get", requestMapping.value()[0]);
        }

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return new RequestMappingInfo("get", getMapping.value()[0]);
        }

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return new RequestMappingInfo("post", postMapping.value()[0]);
        }

        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return new RequestMappingInfo("put", putMapping.value()[0]);
        }

        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return new RequestMappingInfo("delete", deleteMapping.value()[0]);
        }

        // 没有 RequestMapping
        return null;
    }
}

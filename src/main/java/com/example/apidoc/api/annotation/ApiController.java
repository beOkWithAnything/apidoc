package com.example.apidoc.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiController {

    String value() default "";

    // 文档的序号： 按序号从小到大排序
    int order() default 100;
}

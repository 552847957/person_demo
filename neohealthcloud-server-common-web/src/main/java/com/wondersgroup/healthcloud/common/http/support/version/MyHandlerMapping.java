package com.wondersgroup.healthcloud.common.http.support.version;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * Created by zhuchunliu on 2017/8/9.
 */
public class MyHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        VersionRange typeAnnotation = AnnotationUtils.findAnnotation(handlerType, VersionRange.class);
        return createCondition(typeAnnotation);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        VersionRange methodAnnotation = AnnotationUtils.findAnnotation(method, VersionRange.class);
        return createCondition(methodAnnotation);
    }

    private RequestCondition<?> createCondition(VersionRange accessMapping) {
        return accessMapping != null ? new VersionRequestCondition(accessMapping.from(), accessMapping.to(), accessMapping.exclude()) : null;
    }
}

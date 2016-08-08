package com.wondersgroup.healthcloud.common.http.filters.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondersgroup.healthcloud.common.http.annotations.ParaConvert;
import com.wondersgroup.healthcloud.common.http.utils.JsonNameStrategy;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jimmy on 16/8/5.
 */
public class RequestParaConvertInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            ParaConvert paraConvert = method.getMethodAnnotation(ParaConvert.class);
            if (paraConvert != null) {
                WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
                ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
                objectMapper.setPropertyNamingStrategy(new JsonNameStrategy());
            }
        }
        return true;
    }

}

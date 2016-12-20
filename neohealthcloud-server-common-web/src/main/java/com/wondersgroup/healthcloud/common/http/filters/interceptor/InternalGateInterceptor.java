package com.wondersgroup.healthcloud.common.http.filters.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.wondersgroup.common.http.utils.JsonConverter;
import com.wondersgroup.healthcloud.common.http.servlet.ServletAttributeCacheUtil;
import com.wondersgroup.healthcloud.common.http.servlet.ServletRequestIPAddressUtil;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/21/16.
 */
public class InternalGateInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("gatelog");
    private static final String requestStartTimeAttributeKey = "request_start";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long start = System.currentTimeMillis();
        request.setAttribute(requestStartTimeAttributeKey, start);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long end = System.currentTimeMillis();
        Long start = (Long) request.getAttribute(requestStartTimeAttributeKey);

        String method = request.getMethod();
        String URI = request.getServletPath();

        String remoteIp = ServletRequestIPAddressUtil.parse(request);

        Long duration = end - start;

        /*StringBuilder sb = new StringBuilder(512);
        sb.append(method);
        sb.append(URI);
        sb.append(" ");
        sb.append(remoteIp);
        sb.append(" ");
        sb.append(duration);
        sb.append(" ");
        sb.append(response.getStatus());
        sb.append(" ");
        sb.append(request.getQueryString());
        sb.append(" ");
        sb.append(Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8));

        logger.info(sb.toString());*/

        LogBuilder node = LogBuilder.builder();

        node.put("project_id", "0103");//0101=健康云user、0102=健康云disease、0103=健康云internal
        node.put("action_time", System.currentTimeMillis());
        node.put("url", method + URI);
        node.put("ip", remoteIp);
        node.put("duration", duration);
        node.put("status", response.getStatus());
        node.put("request-id", request.getHeader("request-id"));
        node.put("headers", ServletAttributeCacheUtil.getHeaderStr(request));
        //if (!logIgnore.contains(method + URI)) {
            node.put("query", request.getQueryString());
            String body = Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8);
            if (StringUtils.isNotBlank(body)) {
                node.put("body", JsonConverter.toJsonNode(body));
            }
        //}

        Session session = ServletAttributeCacheUtil.getSession(request, null);
        if (session != null) {
            node.put("uid", session.getUserId());
        }else {
            node.put("uid", "");
        }

        logger.info(node.build());

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
    private static class LogBuilder {

        private static final JsonNodeFactory factory = JsonNodeFactory.instance;
        private ObjectNode node;

        LogBuilder() {
            node = factory.objectNode();
        }

        static LogBuilder builder() {
            return new LogBuilder();
        }

        String build() {
            return node.toString();
        }

        LogBuilder put(String key, Object value) {
            if (value == null || StringUtils.isBlank(value.toString())) {
                return this;
            }
            if (value instanceof JsonNode) {
                node.set(key, (JsonNode) value);
            } else {
                if (StringUtils.isNotBlank(value.toString())) {
                    node.put(key, value.toString());
                }
            }
            return this;
        }
    }
}

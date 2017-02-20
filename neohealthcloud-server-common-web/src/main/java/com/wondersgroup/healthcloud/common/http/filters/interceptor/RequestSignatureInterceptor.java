package com.wondersgroup.healthcloud.common.http.filters.interceptor;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.http.servlet.ServletAttributeCacheUtil;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import com.wondersgroup.healthcloud.utils.security.AppSecretKeySelector;
import com.wondersgroup.healthcloud.utils.security.HMAC;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

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
 * Created by zhangzhixiu on 16/4/27.
 */
public final class RequestSignatureInterceptor extends AbstractHeaderInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestSignatureInterceptor.class);

    private final SessionUtil sessionUtil;
    private final AppSecretKeySelector appSecretKeySelector;

    public RequestSignatureInterceptor(SessionUtil sessionUtil, Boolean isSandbox, AppSecretKeySelector appSecretKeySelector) {
        this.sessionUtil = sessionUtil;
        this.isSandbox = isSandbox;
        this.appSecretKeySelector = appSecretKeySelector;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (skipHeaderCheck(request)) {
            return true;
        }

        String signatureFromClient = request.getHeader("signature");
        String method = request.getMethod().toUpperCase();
        String path = request.getServletPath().toLowerCase();

        String requestId = request.getHeader("request-id");
        String timestamp = request.getHeader("client-request-time");
        String timeDiff = request.getHeader("time-diff");
        String timeDiffStr = timeDiff != null ? ("+time-diff=" + timeDiff) : "";
        String headerStr = ServletAttributeCacheUtil.getHeaderStr(request);
        String mainArea = request.getHeader("main-area");
        String appSecret = appSecretKeySelector.get(mainArea);
        Session session = ServletAttributeCacheUtil.getSession(request, sessionUtil);
        String secret = session == null ? "1bc215fa-7a88-4972-a33e-3eb2e37de5ca" : session.getSecret();
        String prefix = "crackerdontdothisplz!\n";
        String parameterStr;
        if ("GET".equals(method) || "DELETE".equals(method)) {
            parameterStr = getMethodParameterStr(request);
        } else {
            parameterStr = postMethodParameterStr(request);
        }
        String signatureStr = prefix + "request-id=" + requestId + "@client-request-time=" + timestamp + timeDiffStr + "\n"
                + path + "+access-token=" + StringUtils.defaultString(request.getHeader("access-token")) + "&" + headerStr + "+" + parameterStr + "\n"
                + appSecret;
        String calculated = HMAC.calculateRFC2104HMAC(signatureStr, secret);
        if (calculated.equals(signatureFromClient)) {
            return true;
        } else {
            logger.info("error signature generate from " + signatureStr);
            buildResponse(response, 21);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

    }

    private String postMethodParameterStr(HttpServletRequest request) throws Exception {
        return Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8);
    }

    private String getMethodParameterStr(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        List<String> queryList = new ArrayList<>();
        List<String> sortedParameterNameList = sortParameterNames(parameterNames);
        for (String name : sortedParameterNameList) {
            queryList.add(String.format("%s=%s", name, request.getParameter(name)));
        }
        return StringUtils.join(queryList, "&");
    }

    private List<String> sortParameterNames(Enumeration<String> parameterNames) {
        List<String> namesList = Lists.newLinkedList();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            ListIterator<String> iterator = namesList.listIterator();
            Boolean inserted = false;
            while (iterator.hasNext()) {
                String s = iterator.next();
                if (parameterName.compareTo(s) < 0) {
                    iterator.previous();
                    iterator.add(parameterName);
                    inserted = true;
                    break;
                }
            }
            if (inserted) {
                continue;
            }
            namesList.add(parameterName);
        }
        return namesList;
    }

    private void buildResponse(HttpServletResponse response, int code) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(String.format("{\"code\":%d}", code));
            writer.close();
        } catch (IOException e) {
            //ignore
        }
    }
}

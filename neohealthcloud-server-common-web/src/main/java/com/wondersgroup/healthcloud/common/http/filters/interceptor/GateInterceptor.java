package com.wondersgroup.healthcloud.common.http.filters.interceptor;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.wondersgroup.healthcloud.common.http.annotations.IgnoreGateLog;
import com.wondersgroup.healthcloud.common.http.servlet.ServletAttributeCacheUtil;
import com.wondersgroup.healthcloud.common.http.servlet.ServletRequestIPAddressUtil;
import com.wondersgroup.healthcloud.common.http.support.version.APIScanner;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
 * <p/>
 * Created by zhangzhixiu on 15/11/17.
 */
public final class GateInterceptor extends AbstractHeaderInterceptor {
    private static final Logger logger = LoggerFactory.getLogger("gatelog");
    private static final String requestStartTimeAttributeKey = "request_start";

    private static final ImmutableSet<String> logIgnore;

    static {
        List<String> parseFromPackage = APIScanner.getAPIsByExistAnnotation("com.wondersgroup.healthcloud", IgnoreGateLog.class);
        logIgnore = ImmutableSet.copyOf(parseFromPackage);
    }

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

        Long durantion = end - start;

        StringBuilder sb = new StringBuilder(512);
        sb.append(method);
        sb.append(URI);
        sb.append(" ");
        sb.append(remoteIp);
        sb.append(" ");
        sb.append(durantion);
        sb.append(" ");
        sb.append(response.getStatus());
        sb.append(" ");
        sb.append(ServletAttributeCacheUtil.getHeaderStr(request));
        if (!logIgnore.contains(method + URI)) {
            sb.append(" ");
            sb.append(request.getQueryString());
            sb.append(" ");
            sb.append(Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8));
        }

        logger.info(sb.toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}

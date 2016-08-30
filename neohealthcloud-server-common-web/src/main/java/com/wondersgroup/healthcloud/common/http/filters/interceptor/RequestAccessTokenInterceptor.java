package com.wondersgroup.healthcloud.common.http.filters.interceptor;

import com.google.common.collect.ImmutableSet;
import com.wondersgroup.common.http.utils.JsonConverter;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.ErrorMessageSelector;
import com.wondersgroup.healthcloud.common.http.support.misc.SessionDTO;
import com.wondersgroup.healthcloud.common.http.support.version.APIScanner;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
 * Created by zhangzhixiu on 16/2/22.
 */
public final class RequestAccessTokenInterceptor extends AbstractHeaderInterceptor {

    private static final String accesstokenHeader = "access-token";

    private static final ImmutableSet<String> exclude;

    private SessionUtil sessionUtil;

    static {
        List<String> parseFromPackage = APIScanner.getAPIsByExistAnnotation("com.wondersgroup.healthcloud", WithoutToken.class);
        exclude = ImmutableSet.copyOf(parseFromPackage);
    }

    public RequestAccessTokenInterceptor(SessionUtil sessionUtil, Boolean isSandbox) {
        this.isSandbox = isSandbox;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (skipHeaderCheck(request)) {
            return true;
        }

        String method = request.getMethod();
        String URI = request.getServletPath();

        if (!ifExclude(method, URI)) {
            int code;
            String message;
            String token = request.getHeader(accesstokenHeader);
            if (token == null) {
                code = 10;
                message = ErrorMessageSelector.getOne();
            } else {//token!=null
                Session session = sessionUtil.get(token);
                if (session == null) {
                    code = 12;
                    message = "登录凭证过期, 请重新登录";
                } else if (!session.getIsValid()) {
                    code = 13;
                    message = "账户在其他设备登录, 请重新登录";
                } else {
                    return true;
                }
            }
            buildGuestResponseBody(response, code, message);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private void buildGuestResponseBody(HttpServletResponse response, int code, String msg) {//return a guest token when token check failed.
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            JsonResponseEntity<SessionDTO> result = new JsonResponseEntity<>();
            result.setCode(code);
            result.setMsg(msg);
            result.setData(new SessionDTO(sessionUtil.createGuest()));
            writer.write(JsonConverter.toJson(result));
            writer.close();
        } catch (IOException e) {
            //ignore
        }
    }

    private boolean ifExclude(String method, String URI) {
        return exclude.contains(method + URI);
    }
}
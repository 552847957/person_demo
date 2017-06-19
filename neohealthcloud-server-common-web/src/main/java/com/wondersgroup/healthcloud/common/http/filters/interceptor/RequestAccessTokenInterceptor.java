package com.wondersgroup.healthcloud.common.http.filters.interceptor;

import com.wondersgroup.common.http.utils.JsonConverter;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.ErrorMessageSelector;
import com.wondersgroup.healthcloud.common.http.servlet.ServletAttributeCacheUtil;
import com.wondersgroup.healthcloud.common.http.support.OverAuthExclude;
import com.wondersgroup.healthcloud.common.http.support.misc.SessionDTO;
import com.wondersgroup.healthcloud.common.http.support.parms.QueryParmUtils;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

    private SessionUtil sessionUtil;

    private OverAuthExclude overAuthExclude;

    public RequestAccessTokenInterceptor(SessionUtil sessionUtil, Boolean isSandbox, OverAuthExclude overAuthExclude) {
        this.isSandbox = isSandbox;
        this.sessionUtil = sessionUtil;
        this.overAuthExclude = overAuthExclude;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (skipHeaderCheck(request)) {
            return true;
        }

        int code = 0;
        String message = "";
        String uid = "";
        String token = request.getHeader(accesstokenHeader);
        if (StringUtils.isBlank(token)) {
            code = 10;
            message = ErrorMessageSelector.getOne();
        } else {//token!=null
            Session session = ServletAttributeCacheUtil.getSession(request, sessionUtil);
            if (session == null) {
                code = 12;
                message = "登录凭证过期, 请重新登录";
            } else if (!session.getIsValid()) {
                code = 13;
                message = "账户在其他设备登录, 请重新登录";
            } else if (session.isGuest()) {
                code = 1000;
                message = "请登录";
            } else if(RequestMethod.GET.toString().equals(request.getMethod())||
                    RequestMethod.DELETE.toString().equals(request.getMethod())){
                uid = QueryParmUtils.filterUid(QueryParmUtils.getGetOrDeleteRequestQueryParam(request));
            } else if(RequestMethod.POST.toString().equals(request.getMethod())){
                uid = QueryParmUtils.filterUid(QueryParmUtils.getPostRequestQueryParam(request));
            }
            String appName=request.getHeader("app-name");
            if(StringUtils.isNotBlank(appName)&&appName.equals("com.wondersgroup.healthcloud.3101://doctor")){
                if(!overAuthExclude.isExcludeForDoctor(request.getServletPath())){
                    if(!StringUtils.isEmpty(uid)){
                        if(!checkOverAuth(uid, session)){
                            code = 13;
                            message = "账户在其他设备登录, 请重新登录";
                        }
                    }
                }
            }else if(!overAuthExclude.isExclude(request.getServletPath())){
                if(!StringUtils.isEmpty(uid)){
                    if(!checkOverAuth(uid, session)){
                        code = 13;
                        message = "账户在其他设备登录, 请重新登录";
                    }
                }
            }
        }

        boolean excluded = false;
        if (o instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) o;
            excluded = hm.getMethodAnnotation(WithoutToken.class) != null;
        }

        if ((!excluded && code == 12) || (!excluded && code == 13) || (!excluded && code == 10)) {
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

    private boolean checkOverAuth(String uid, Session session){
        if(session.getUserId().equalsIgnoreCase(uid))
            return true;
        else
            return false;

    }
}
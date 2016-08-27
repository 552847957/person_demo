package com.wondersgroup.healthcloud.api.shiro;

import com.wondersgroup.healthcloud.api.helper.RSAEncryptUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;

/**
 * Created by Administrator on 2015/12/14.
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = this.getUsername(request);
        String password = this.getPassword(request);
        HttpServletRequest rq = (HttpServletRequest)request;
        KeyPair keyPair = (KeyPair) rq.getSession().getAttribute("keyPair");
        if (keyPair != null) {
            if (!StringUtils.isEmpty(username)) {
                username = RSAEncryptUtils.decryptStringByJavaScript(keyPair, username);
            }
            if (!StringUtils.isEmpty(password)) {
                password = RSAEncryptUtils.decryptStringByJavaScript(keyPair, password);
            }
        }
        return this.createToken(username, password, request, response);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if(request.getAttribute(getFailureKeyAttribute()) != null) {
            return true;
        }
        return super.onAccessDenied(request, response, mappedValue);
    }


}

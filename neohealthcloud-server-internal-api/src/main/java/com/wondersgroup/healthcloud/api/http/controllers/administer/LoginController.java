package com.wondersgroup.healthcloud.api.http.controllers.administer;

/*import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.helper.UserHelper;*/
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
/*import com.wondersgroup.healthcloud.jpa.entity.permission.User;*/
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
/*import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/10.
 */
@RestController
@RequestMapping(value = "/api")
public class LoginController {

    /*@Autowired
    private UserHelper userHelper;

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public JsonResponseEntity welcome() {
        JsonResponseEntity result = new JsonResponseEntity();
        User user = userHelper.getCurrentUser();
        if (user != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("mainArea", user.getMainArea());
            map.put("specArea", user.getSpecArea());
            map.put("userName", user.getUsername());
            result.setData(map);
        }
        return result;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public JsonResponseEntity logout() {
        SecurityUtils.getSubject().logout();
        JsonResponseEntity result = new JsonResponseEntity();
        result.setMsg("logout success");
        return result;
    }*/

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public JsonResponseEntity login(HttpServletRequest request) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map<String, Object> map = new HashMap<>();
        map.put("mainArea", "4401");
        //map.put("specArea", null);
        map.put("userName", "admin");
        map.put("publicKey", HttpWdUtils.publicKey);
        result.setData(map);
       /*if(SecurityUtils.getSubject().isAuthenticated()) {
            //return new ModelAndView("redirect:/welcome");
            result.setCode(0);
            result.setData(SecurityUtils.getSubject().getPrincipal());
            return result;
        }
        String exceptionClassName = (String)request.getAttribute("shiroLoginFailure");
        String error = null;
        if(DisabledAccountException.class.getName().equals(exceptionClassName)){
            error = "账号已被禁用";
        }else if(UnknownAccountException.class.getName().equals(exceptionClassName)) {
            error = "账号不存在";
        } else if(IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            error = "密码错误";
        } else if(ExcessiveAttemptsException.class.getName().equals(exceptionClassName)){
            error = "重复登录错误";
        } else if(exceptionClassName != null) {
            error = "其他错误";
        } else {
            error = "账号未登录";
        }
        result.setCode(1000);
        result.setMsg(error);*/
        return result;
    }
}

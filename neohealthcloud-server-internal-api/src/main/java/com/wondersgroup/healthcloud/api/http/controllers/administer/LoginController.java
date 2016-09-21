package com.wondersgroup.healthcloud.api.http.controllers.administer;

import com.wondersgroup.healthcloud.api.helper.UserHelper;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.app.AppKeyConfigurationInfo;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.services.permission.BasicInfoService;
import com.wondersgroup.healthcloud.services.permission.dto.MenuDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/10.
 */
@RestController
@RequestMapping(value = "/api")
public class LoginController {

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private BasicInfoService basicInfoService;

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponseEntity welcome() {
        JsonResponseEntity result = new JsonResponseEntity();
        User user = userHelper.getCurrentUser();
        if (user != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("mainArea", user.getMainArea());
            map.put("sessionId", SecurityUtils.getSubject().getSession().getId());
            if (user.getSpecArea() != null) {
                map.put("specArea", user.getSpecArea());
            }
            map.put("userId", user.getUserId());
            map.put("userName", user.getUsername());
            AppKeyConfigurationInfo appKCfg = userHelper.getKeyCfgByArea(user.getMainArea());
            if (appKCfg != null) {
                map.put("areaName", appKCfg.getName());
            }
            MenuDTO menu = basicInfoService.findUserMunuPermission(user.getUserId());
            if (menu != null) {
                map.put("menu", menu);
            }

            result.setData(map);
        }
        return result;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public JsonResponseEntity logout() {
        SecurityUtils.getSubject().logout();
        JsonResponseEntity result = new JsonResponseEntity();
        result.setMsg("账号退出成功！");
        return result;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JsonResponseEntity login(HttpServletRequest request) {
        JsonResponseEntity result = new JsonResponseEntity();

        if (SecurityUtils.getSubject().isAuthenticated()) {
            User user = userHelper.getCurrentUser();
            if (user != null) {
                //return new ModelAndView("redirect:/welcome");
                Map<String, Object> map = new HashMap<>();
                map.put("mainArea", user.getMainArea());
                if (user.getSpecArea() != null) {
                    map.put("specArea", user.getSpecArea());
                }
                map.put("userName", user.getUsername());
                AppKeyConfigurationInfo appKCfg = userHelper.getKeyCfgByArea(user.getMainArea());
                if (appKCfg != null) {
                    map.put("areaName", appKCfg.getName());
                }
                result.setData(map);
            }
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
        result.setMsg(error);
        return result;
    }
}

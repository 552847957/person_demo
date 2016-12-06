package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by ys on 16/8/18.
 * 话题圈子相关
 *
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/admin")
public class BbsAdminController {

    private static final Logger logger = LoggerFactory.getLogger("BbsAdminController");
    @Autowired
    private UserAccountService userAccountService;
    /**
     * 绑定手机段用户
     * @return
     */
    @Admin
    @RequestMapping(value = "/bindAppUser", method = RequestMethod.POST)
    public JsonResponseEntity bindAppUser(@RequestHeader String adminid, @RequestBody String request) {
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String mobile = reader.readString("mobile", false);
        String code = reader.readString("code", false);

        Boolean result = userAccountService.validateCode(mobile, code, false);
        if (!result){
            throw new CommonException(1001, "短信验证码验证错误");
        }
        entity.setMsg("绑定成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/sendPhoneCode", method = RequestMethod.POST)
    public JsonResponseEntity sendPhoneCode(@RequestParam String mobile) {
        JsonResponseEntity entity = new JsonResponseEntity();
        userAccountService.getVerifyCode(mobile, 3);
        entity.setMsg("短信验证码发送成功");
        return entity;
    }

}

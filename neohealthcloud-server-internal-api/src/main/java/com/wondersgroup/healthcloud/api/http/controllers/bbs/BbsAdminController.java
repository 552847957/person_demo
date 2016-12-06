package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    /**
     * 绑定手机段用户
     * @return
     */
    @Admin
    @RequestMapping(value = "/bindAppUser", method = RequestMethod.POST)
    public JsonResponseEntity bindAppUser(@RequestHeader String userId, @RequestBody String request) {
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String mobile = reader.readString("mobile", false);
        String code = reader.readString("code", false);
        checkIsCanBindForAdmin(userId, mobile);

        Boolean result = userAccountService.validateCode(mobile, code, false);
        if (!result){
            throw new CommonException(1001, "短信验证码验证错误");
        }
        entity.setMsg("绑定成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/sendPhoneCode", method = RequestMethod.POST)
    public JsonResponseEntity sendPhoneCode(@RequestHeader String userId, @RequestParam String mobile) {

        JsonResponseEntity entity = new JsonResponseEntity();
        checkIsCanBindForAdmin(userId, mobile);
        userAccountService.getVerifyCode(mobile, 3);
        entity.setMsg("短信验证码发送成功");
        return entity;
    }

    private void checkIsCanBindForAdmin(String adminId, String mobile){
        RegisterInfo registerInfo = userService.findRegisterInfoByMobile(mobile);
        if (null == registerInfo){
            throw new CommonException(2002, "手机号没有注册!");
        }
        User bindUser = userRepository.findByBindUid(registerInfo.getRegisterid());
        if (null != bindUser && !bindUser.getUserId().equals(adminId)){
            throw new CommonException(2003, "该手机号已被其他管理员绑定!");
        }
    }
}

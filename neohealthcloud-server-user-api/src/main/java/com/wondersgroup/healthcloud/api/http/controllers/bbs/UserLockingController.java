package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.RequestDataReader;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.services.bbs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 禁言
 * Created by jialing.yao on 2016-8-15.
 */
@RestController
@RequestMapping("/api/bbs")
public class UserLockingController {

    @Autowired
    private UserBbsService userBbsService;
    @Autowired
    SysMsgService sysMsgService;

    /**
     * 根据UID，获取用户禁言信息
     */
    @RequestMapping(value = "/userlocking/search", method = RequestMethod.GET)
    public Object getUserLockingInfo(@RequestParam String uid){
        Map<String,Object> ret= userBbsService.getUserBanInfoByUid(uid);
        JsonResponseEntity<Map<String,Object>> responseEntity = new JsonResponseEntity<>();
        responseEntity.setData(ret);
        return responseEntity;
    }

    /**
     * 设置禁言
     */
    @RequestMapping(value = "/userlocking/enable", method = RequestMethod.POST)
    public Object enableUserLocking(@RequestBody(required = false) String body){
        RequestDataReader reader = new RequestDataReader(body);
        String loginUid=reader.readString("loginUid",false);
        String uid=reader.readString("uid",false);
        Integer expire=reader.readInteger("expire", false);
        String reason=reader.readDefaultString("reason","");

        JsonResponseEntity<Map<String,String>> responseEntity = new JsonResponseEntity<>();
        if (!UserConstant.BanStatus.isVaildStatus(expire)){
            responseEntity.setCode(1000);
            responseEntity.setMsg("禁言-时效类型["+expire+"]不匹配.");
        }
        Boolean isOK = userBbsService.setUserBan(loginUid, uid, expire, reason);

        if(isOK) {
            responseEntity.setCode(0);
            responseEntity.setMsg("设置禁言成功.");
        }else {
            responseEntity.setCode(1000);
            responseEntity.setMsg("设置禁言失败.");
        }
        return responseEntity;
    }

    /**
     * 解除禁言
     */
    @RequestMapping(value = "/userlocking/disable", method = RequestMethod.POST)
    public Object disableUserLocking(@RequestBody(required = false) String body){
        RequestDataReader reader = new RequestDataReader(body);
        String loginUid=reader.readString("loginUid",false);
        String uid=reader.readString("uid",false);

        Boolean isOK = userBbsService.setUserBan(loginUid, uid, UserConstant.BanStatus.OK, "");

        JsonResponseEntity<Map<String,String>> responseEntity = new JsonResponseEntity<>();
        if(isOK) {
            responseEntity.setCode(0);
            responseEntity.setMsg("解除禁言成功.");
        }else {
            responseEntity.setCode(1000);
            responseEntity.setMsg("解除禁言失败.");
        }
        return responseEntity;
    }

    /**
     * 禁言详情
     */
    @RequestMapping(value = "/userlocking/detail", method = RequestMethod.GET)
    public Object disableUserLocking(@RequestParam int banID){
        Map<String, Object> data=sysMsgService.getUserBanInfoByUid(banID);
        JsonResponseEntity<Map<String, Object>> responseEntity = new JsonResponseEntity<>();
        responseEntity.setData(data);
        return responseEntity;
    }
}

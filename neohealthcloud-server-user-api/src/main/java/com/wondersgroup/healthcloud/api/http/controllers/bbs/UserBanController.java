package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.RequestDataReader;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserBanLog;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.UserBanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户禁言设置
 * Created by jialing.yao on 2016-8-15.
 */
@RestController
@RequestMapping("/api/bbs")
public class UserBanController {

    @Autowired
    private UserBbsService userBbsService;

    /**
     * 获取用户当前的禁言信息
     */
    @VersionRange
    @RequestMapping(value = "/userlocking/search", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String,Object>> getUserLockingInfo(@RequestParam String uid){
        UserBanLog banInfo = userBbsService.getUserBanInfoByUid(uid);
        JsonResponseEntity<Map<String,Object>> entity = new JsonResponseEntity<>();
        if (banInfo == null){
            return entity;
        }
        Map<String,Object> info = new HashMap<>();
        info.put("expire", banInfo.getBanStatus());
        info.put("reason", banInfo.getReason());
        entity.setData(info);
        return entity;
    }

    /**
     * 设置禁言
     */
    @VersionRange
    @RequestMapping(value = "/userlocking/enable", method = RequestMethod.POST)
    public Object enableUserLocking(@RequestBody(required = false) String body){
        RequestDataReader reader = new RequestDataReader(body);
        String loginUid=reader.readString("loginUid",false);
        String uid=reader.readString("uid",false);
        Integer expire=reader.readInteger("expire", false);
        String reason=reader.readDefaultString("reason","");

        JsonResponseEntity<Map<String,String>> responseEntity = new JsonResponseEntity<>();
        if (!UserConstant.BanStatus.isVaildStatus(expire)){
            throw new CommonException(2001, "禁言时效无效");
        }
        Boolean isOK = userBbsService.setUserBan(loginUid, uid, expire, reason);
        if(isOK) {
            responseEntity.setCode(0);
            responseEntity.setMsg("设置禁言成功.");
        }else {
            responseEntity.setCode(2040);
            responseEntity.setMsg("设置禁言失败.");
        }
        return responseEntity;
    }

    /**
     * 解除禁言
     */
    @VersionRange
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
    @VersionRange
    @RequestMapping(value = "/userlocking/detail", method = RequestMethod.GET)
    public JsonResponseEntity<UserBanInfo> banDetail(@RequestParam int banID){
        UserBanInfo banInfo = userBbsService.getUserBanInfoByBanLogId(banID);
        JsonResponseEntity<UserBanInfo> responseEntity = new JsonResponseEntity<>();
        responseEntity.setData(banInfo);
        return responseEntity;
    }
}

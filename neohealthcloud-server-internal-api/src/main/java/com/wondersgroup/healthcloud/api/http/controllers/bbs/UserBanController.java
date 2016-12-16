package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.services.bbs.UserBbsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 禁言
 * Created by ys on 2016-12-14.
 */
@RestController
@RequestMapping("/api/bbs/userBan")
public class UserBanController {

    @Autowired
    private UserBbsService userBbsService;

    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    public JsonResponseEntity disable(@RequestHeader String appUid, @RequestBody String request){
        JsonResponseEntity<Map<String,String>> entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        Integer banStatus = reader.readInteger("banStatus", false);
        String reason = reader.readDefaultString("reason","");

        if (!UserConstant.BanStatus.isVaildStatus(banStatus)){
            entity.setCode(1000);
            entity.setMsg("禁言-时效类型["+banStatus+"]不匹配.");
        }
        userBbsService.setUserBan(appUid, uid, banStatus, reason);
        entity.setMsg("设置禁言成功.");
        return entity;
    }

    @RequestMapping(value = "/free", method = RequestMethod.POST)
    public JsonResponseEntity free(@RequestHeader String appUid, @RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        userBbsService.setUserBan(appUid, uid, UserConstant.BanStatus.OK, "");
        JsonResponseEntity entity = new JsonResponseEntity<>();
        entity.setMsg("解除禁言成功.");
        return entity;
    }
}

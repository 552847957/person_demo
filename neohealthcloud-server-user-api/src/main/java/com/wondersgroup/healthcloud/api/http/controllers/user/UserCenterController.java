package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.bbs.UserBbsService;
import com.wondersgroup.healthcloud.services.bbs.UserFansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dukuanxin on 2016/12/12.
 */
@RestController
@RequestMapping("/api/user")
public class UserCenterController {


    @Autowired
    private UserFansService fansService;

    @Autowired
    private UserBbsService userBbsService;

    /**
     * 个人中心首页接口
     * @param uid
     * @return
     */
    @VersionRange
    @GetMapping("/center/home")
    public JsonResponseEntity userCenterHome(@RequestParam String uid){
        JsonResponseEntity response=new JsonResponseEntity();
        int concernCount=fansService.countAttentNum(uid);//我的关注数
        int fansCount=fansService.countFansNum(uid);//我的粉丝数
        int topicCount=userBbsService.countTopicByUid(uid);//我发表的话题数
        int replyCount=userBbsService.countCommentByUid(uid);//我的回复数
        Map data=new HashMap<>();
        data.put("concernCount",concernCount);
        data.put("fansCount",fansCount);
        data.put("topicCount",topicCount);
        data.put("replyCount",replyCount);
        response.setData(data);
        return response;
    }

}

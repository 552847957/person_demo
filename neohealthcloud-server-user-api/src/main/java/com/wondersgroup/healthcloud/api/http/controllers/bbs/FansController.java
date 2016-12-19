package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.http.dto.bbs.UserFansAttentListDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserFans;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.UserBbsInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 粉丝关注接口
 * Created by ys on 2016-12-09.
 */
@RestController
@RequestMapping("/api/bbs/fans")
public class FansController {

    private static final Logger logger = LoggerFactory.getLogger("FansController");

    @Autowired
    private UserFansService fansService;

    private static final int pageSize = 10;

    /**
     * 粉丝列表查询
     */
    @VersionRange
    @RequestMapping(value = "/fansList", method = RequestMethod.GET)
    public JsonListResponseEntity fansList(@RequestParam String uid, @RequestParam(required = false) String targetUid,
                                              @RequestParam(required = false,defaultValue = "1") Integer flag) {
        if (StringUtils.isEmpty(targetUid)){
            targetUid = uid;
        }
        JsonListResponseEntity entity = new JsonListResponseEntity<>();
        List<UserFansAttentListDto> listDtos = new ArrayList<>();
        List<UserBbsInfo> fansUsers = fansService.getFansUsers(targetUid, flag, pageSize);
        if (fansUsers == null){
            return entity;
        }
        Boolean hasMore = false;
        if (fansUsers.size() > pageSize){
            fansUsers = fansUsers.subList(0, pageSize);
            hasMore = true;
        }
        List<String> fansUids = new ArrayList<>();
        for (UserBbsInfo userBbsInfo : fansUsers){
            fansUids.add(userBbsInfo.getUid());
        }
        Map<String, Integer> myAttentStatus = fansService.getMyAttentStatus(uid, fansUids);
        for (UserBbsInfo userBbsInfo : fansUsers){
            UserFansAttentListDto userFansAttentList = new UserFansAttentListDto(userBbsInfo);
            userFansAttentList.setAttentStatus(myAttentStatus.get(userBbsInfo.getUid()));
            listDtos.add(userFansAttentList);
        }
        entity.setContent(listDtos, hasMore, null, String.valueOf(flag + 1));
        return entity;
    }

    /**
     * 关注列表
     */
    @VersionRange
    @RequestMapping(value = "/attentList", method = RequestMethod.GET)
    public JsonListResponseEntity attentList(@RequestParam String uid, @RequestParam(required = false) String targetUid,
                                              @RequestParam(required = false,defaultValue = "1") Integer flag) {
        if (StringUtils.isEmpty(targetUid)){
            targetUid = uid;
        }
        JsonListResponseEntity entity = new JsonListResponseEntity<>();
        List<UserFansAttentListDto> listDtos = new ArrayList<>();
        List<UserBbsInfo> attentlist = fansService.getAttentUsers(targetUid, flag, pageSize);
        if (attentlist == null){
            return entity;
        }
        Boolean hasMore = false;
        if (attentlist.size() > pageSize){
            attentlist = attentlist.subList(0, pageSize);
            hasMore = true;
        }
        List<String> fansUids = new ArrayList<>();
        for (UserBbsInfo userBbsInfo : attentlist){
            fansUids.add(userBbsInfo.getUid());
        }
        Map<String, Integer> myAttentStatus = fansService.getMyAttentStatus(uid, fansUids);
        for (UserBbsInfo userBbsInfo : attentlist){
            UserFansAttentListDto userFansAttentList = new UserFansAttentListDto(userBbsInfo);
            userFansAttentList.setAttentStatus(myAttentStatus.get(userBbsInfo.getUid()));
            listDtos.add(userFansAttentList);
        }
        entity.setContent(listDtos, hasMore, null, String.valueOf(flag + 1));
        return entity;
    }


    /**
     * 关注某人
     */
    @VersionRange
    @RequestMapping(value = "/attent", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> attent(@RequestBody String request) {
        JsonResponseEntity<Map<String, Object>> entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String targetUid = reader.readString("targetUid", false);

        Date nowDate = new Date();
        // 注意：关注别人，fans_uid 为自己
        UserFans exist = fansService.queryByUidAndFansUid(targetUid, uid);
        Map<String, Object> info = new HashMap<>();
        entity.setMsg("关注成功");
        if (exist != null) {// 已经关注过
            if ("0".equals(exist.getDelFlag())) {
                entity.setMsg("您已关注了该用户");
            }else {
                exist.setDelFlag("0");
                exist.setUpdateTime(nowDate);
                fansService.saveFans(exist);// 更新del_flag状态
            }
        } else {
            UserFans fans = new UserFans();
            fans.setUid(targetUid);
            fans.setFansUid(uid);
            fans.setCreateTime(nowDate);
            fans.setUpdateTime(nowDate);
            fansService.saveFans(fans);
        }
        info.put("attentStatus", fansService.getMyAttentStatus(uid, targetUid));
        entity.setData(info);
        return entity;
    }


    /**
     * 取消关注
     */
    @VersionRange
    @RequestMapping(value = "/attent", method = RequestMethod.DELETE)
    public JsonResponseEntity cancelAttent(@RequestParam String uid, @RequestParam String targetUid) {
        JsonResponseEntity entity = new JsonResponseEntity();
        // 注意，查询关注，fansUid为自己
        UserFans exist = fansService.queryByUidAndFansUid(targetUid, uid);
        if (exist != null && exist.getDelFlag().equals("0")) {
            exist.setDelFlag("1");
            exist.setUpdateTime(new Date());
            fansService.saveFans(exist);
            entity.setMsg("取消关注成功");
            return entity;
        } else {
            entity.setMsg("您未关注该用户");
            return entity;
        }
    }
}

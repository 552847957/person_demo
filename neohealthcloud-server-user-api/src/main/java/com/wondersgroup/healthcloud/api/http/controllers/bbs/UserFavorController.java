package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.NumberUtils;
import com.wondersgroup.healthcloud.services.bbs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 话题点赞 add by ys 2016-10-24
 * @author yanshuai
 */
@RestController
@RequestMapping("/api/bbs/favor")
public class UserFavorController {

    @Autowired
    private FavorService favorService;

    /**
     * 话题点赞
     */
    @VersionRange
    @RequestMapping(value = "/topic", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> RequestMethod(@RequestBody String request){
        JsonResponseEntity<Map<String, Object>> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer topicId = reader.readInteger("topicId", false);
        String uid = reader.readString("uid", false);
        Map<String, Object> info = new HashMap<>();
        int favorCount = favorService.favorTopic(uid, topicId);
        info.put("favorCount", NumberUtils.formatCustom1(favorCount));
        entity.setData(info);
        return entity;
    }

}

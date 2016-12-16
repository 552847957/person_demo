package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.services.home.impl.TopicManageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页-话题管理 <br/>
 * 1、话题配置接口 (走离散配置接口/api/appConfig/saveAppConfig)
 * 2、根据话题ID，查询话题标题接口
 * Created by jialing.yao on 2016-12-14.
 */
@RestController
@RequestMapping("/api/home")
public class TopicManageController {

    /*
    1、话题配置接口
        key_word为app.home.topicmanage
        data为
        {
            "hotTopicBox": [
                21,
                12
            ]
        }
     */
    @Autowired
    private TopicManageServiceImpl topicManageService;

    @Admin
    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String,Object>> getTopicInfo(@RequestParam String topicID) {
        JsonResponseEntity<Map<String,Object>> result = new JsonResponseEntity<>();
        Map<String, Object> topicInfo=topicManageService.findTopicInfoByTopicID(topicID);
        if(topicInfo == null){
            result.setData(new HashMap());
            return result;
        }
        result.setData(topicInfo);
        return result;
    }
}

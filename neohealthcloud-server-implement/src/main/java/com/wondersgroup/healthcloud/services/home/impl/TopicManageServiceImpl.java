package com.wondersgroup.healthcloud.services.home.impl;

import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.services.bbs.TopicService;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicListDto;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.home.apachclient.JsonConverter;
import com.wondersgroup.healthcloud.services.home.dto.topic.TopicConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 首页-热门话题
 * Created by jialing.yao on 2016-12-14.
 */
@Service("topicManageService")
public class TopicManageServiceImpl {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AppConfigService appConfigService;//离散配置
    @Autowired
    private TopicService topicService;

    /**
     * 后台系统-话题管理
     */
    public Map<String, Object> findTopicInfoByTopicID(String topicID) {
        String query =String.format("select id as topicID,title as topicTitle from tb_bbs_topic where id=%s",topicID);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        Map<String, Object> data;
        if (null == list || list.isEmpty()){
            return null;
        }else {
            data = list.get(0);
        }
        return data;
    }

    /**
     * 用户端-首页-热门话题
     */
    public List<TopicListDto> getHotTopicList(String uid,String mainArea){
        //获取管理后台配置的热门话题
        String keyWord="app.home.topicmanage";
        String source="1";//1-用户端;2-医生端
        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, keyWord, source);
        if(appConfig == null){
            return new ArrayList<>();
        }
        TopicConfigDto topicConfigDto=JsonConverter.toObject(appConfig.getData(), TopicConfigDto.class);
        Integer []hotTopocBox=topicConfigDto.getHotTopicBox();
        List<Integer> topicIds= Arrays.asList(hotTopocBox);
        //根据话题ID，获取话题信息
        List<TopicListDto> listDtos = topicService.getTopicsByIds(topicIds);
        if(listDtos == null){
            return new ArrayList<>();
        }
        return listDtos;
    }
}

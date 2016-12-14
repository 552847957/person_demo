package com.wondersgroup.healthcloud.services.home.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 首页-话题管理
 * Created by jialing.yao on 2016-12-14.
 */
@Service("topicManageService")
public class TopicManageServiceImpl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}

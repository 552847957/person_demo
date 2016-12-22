package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicTabSearchCriteria;

import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/08/19.
 * @author ys
 */
public interface TopicTabService {

    /**
     * 获取话题所属标签
     */
    List<TopicTab> getTopicTabs(Integer topicId);

    Boolean updateTopicTabMapInfo(Integer topicId, List<Integer> tabIds);

    List<Map<String, Object>> getTopicTabListByCriteria(TopicTabSearchCriteria searchCriteria);

    int countTopicTabByCriteria(TopicTabSearchCriteria searchCriteria);

}

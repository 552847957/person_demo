package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.services.bbs.criteria.TopicTabSearchCriteria;

import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/08/19.
 * @author ys
 */
public interface TopicTabService {

    Boolean updateTopicTabMapInfo(Integer topicId, List<Integer> tabIds);

    List<Map<String, Object>> getTopicTabListByCriteria(TopicTabSearchCriteria searchCriteria);

    int countTopicTabByCriteria(TopicTabSearchCriteria searchCriteria);

}

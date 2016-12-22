package com.wondersgroup.healthcloud.jpa.repository.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTabMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ys on 2016/08/11
 * 话题标签映射
 */
public interface TopicTabMapRepository extends JpaRepository<TopicTabMap, Integer> {

    @Query("select a from TopicTabMap a where a.topicId=?1")
    List<TopicTabMap> getListByTopicId(Integer topicId);

}

package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 话题详情
 */
public interface TopicContentRepository extends JpaRepository<TopicContent, Integer> {

    @Query(nativeQuery = true,
            value="select * from tb_bbs_topic_content a where a.topic_id=?1 order by id asc")
    List<TopicContent> findContentsByTopicId(Integer topicId);
}

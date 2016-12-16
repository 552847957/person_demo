package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicFavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by ys on 2016/10/21
 * 用户话题点赞
 */
public interface TopicFavorRepository extends JpaRepository<TopicFavor, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_topic_favor a where a.uid=?1 and a.topic_id=?2 limit 1")
    TopicFavor findByUidAndTopicId(String uid, int topicId);

}

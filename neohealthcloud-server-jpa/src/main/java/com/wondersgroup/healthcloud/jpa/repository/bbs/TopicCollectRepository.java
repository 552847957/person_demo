package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 话题
 */
public interface TopicCollectRepository extends JpaRepository<TopicCollect, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_topic_collect a where a.uid=?1 and a.del_flag='0' ORDER BY a.create_time desc limit ?2,?3")
    List<TopicCollect> findListByUid(String uid, int offset, int limit);

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_topic_collect a where a.uid=?1 and a.topic_id=?2 limit 1")
    TopicCollect findInfoByUidAndTopicId(String uid, Integer topicId);
}

package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;
import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 话题
 */
public interface TopicRepository extends JpaRepository<Topic, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_topic a where a.uid=?1 and a.status=1 ORDER BY a.create_time desc limit ?2,?3")
    List<Topic> findListByUid(String uid, int offset, int limit);

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_topic a where a.circle_id=?1 and a.status=1 ORDER BY a.last_comment_time desc limit ?2,?3")
    List<Topic> findListByCicleId(Integer cirleId, int offset, int limit);

    @Query(nativeQuery = true,
            value = "select a.* from tb_bbs_topic a left join tb_bbs_topic_tab_map b on a.id=b.topic_id" +
                    " where a.circle_id=?1 and a.status=1 and b.tab_id=?2 ORDER BY a.last_comment_time desc limit ?3,?4")
    List<Topic> findListByCicleTab(Integer cirleId, Integer tabId, int offset, int limit);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update tb_bbs_topic t set t.pv=t.pv+1 where t.id=?1")
    void incTopicPv(Integer topicId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Topic t set t.status=?1 where t.id in ?2")
    void multSettingStatus(Integer status, Iterable<Integer> topicIds);
}

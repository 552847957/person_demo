package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ys on 2016/08/11
 * 话题标签
 */
public interface TopicTabRepository extends JpaRepository<TopicTab, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_topic_tab a where circle_id=?1 and del_flag='0' order by a.rank desc ")
    List<TopicTab> getTopicTabsByCircleId(Integer circleId);
}

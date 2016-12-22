package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 话题评论
 */
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_comment a where a.topic_id=?1 and a.floor>=?2 order by floor asc limit ?3")
    List<Comment> findListByTopicAndFloor(int topicId, int floor, int pageSize);

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_comment a where a.uid=?1 order by a.create_time desc limit ?2,?3")
    List<Comment> findListByUid(String uid, int offset, int pageSize);

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_comment a where a.topic_id=?1 and a.is_owner=1 order by a.floor asc limit ?2,?3")
    List<Comment> findTopicOwnerComments(Integer topicId, int offset, int pageSize);

    @Query("select count(id) from Comment a where a.uid=?1 and a.status in ?2")
    int countByReplyUidAndStatus(String uid, Iterable<Integer>  status);

    @Query(nativeQuery = true,
            value = "select count(*) from tb_bbs_comment a where a.topic_id=?1 and a.is_owner=1")
    Integer getTopicOwnerReplyCount(Integer topicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Comment a set a.status=?1 where a.id in ?2")
    void updateStatusByIds(Integer status, Iterable<Integer> ids);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Comment a set a.status=?1 where a.id = ?2")
    void updateStatusById(Integer status, Integer ids);
}

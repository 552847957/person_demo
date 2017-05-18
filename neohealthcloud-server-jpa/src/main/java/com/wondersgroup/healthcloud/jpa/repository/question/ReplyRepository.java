package com.wondersgroup.healthcloud.jpa.repository.question;

import com.wondersgroup.healthcloud.jpa.entity.question.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, String> {

    /**
     * 获取医生用户聊天的最后一个问题回复，
     */
    @Query(nativeQuery = true, value = "SELECT c.* from app_tb_neoreply c left join app_tb_neogroup cg ON c.comment_group_id=cg.id " +
            " WHERE cg.question_id=?1 and cg.answer_id=?2 and cg.is_valid=1 ORDER BY c.create_time DESC limit 1")
    Reply getCommonGroupLastReply(String question_id, String doctor_id);

    /**
     * 获取问题回复
     */
    @Query(nativeQuery = true, value = "SELECT c.* from app_tb_neoreply c where c.comment_group_id in ?1 and c.is_valid=1 order by c.create_time desc")
    List<Reply> getCommentGroupList(List<String> group_ids);

    /**
     * 获取分组下的对话
     */
    @Query(nativeQuery = true, value = "SELECT c.* from app_tb_neoreply c where c.comment_group_id = ?1 and c.is_valid=1 order by c.create_time desc")
    List<Reply> getReplyByGroupId(String groupId);
}

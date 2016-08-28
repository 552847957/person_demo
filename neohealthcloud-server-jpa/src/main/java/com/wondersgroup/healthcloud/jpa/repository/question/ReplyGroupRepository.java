package com.wondersgroup.healthcloud.jpa.repository.question;


import com.wondersgroup.healthcloud.jpa.entity.question.ReplyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyGroupRepository extends JpaRepository<ReplyGroup, String> {

    /**
     * 获取问题回复组
     */
    @Query(nativeQuery = true, value = "SELECT cg.* from app_tb_neogroup cg where cg.question_id=?1 and cg.is_valid=1 ORDER BY cg.create_time desc ")
    List<ReplyGroup> getCommentGroupList(String question_id);

    @Query(nativeQuery = true, value = "SELECT cg.* from app_tb_neogroup cg where cg.question_id=?1 " +
            " and cg.answer_id=?2 and cg.is_valid=1 limit 1")
    ReplyGroup getCommentGroup(String question_id, String doctor_id);
}

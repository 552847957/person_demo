package com.wondersgroup.healthcloud.jpa.repository.medicalcircle;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleReply;

public interface MedicalCircleReplyRepository extends JpaRepository<MedicalCircleReply,String>{

    @Query(value = "select m from MedicalCircleReply m where m.id=?1 and m.replyid=?2 and m.delFlag=0 ")
    MedicalCircleReply findReplyWithUser(String replyId,String replyDocotrId);

    @Query(value = "select m from MedicalCircleReply m where m.communityid=?1 and m.discusstime>?2 and m.delFlag=0")
    Page<MedicalCircleReply> findCommentReplyList(String commentId,Date flag,Pageable pageable);


}

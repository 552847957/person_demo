package com.wondersgroup.healthcloud.jpa.repository.medicalcircle;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCommunity;

public interface MedicalCircleCommunityRepository extends JpaRepository<MedicalCircleCommunity,String>{

    @Query(value = "select count(1) from MedicalCircleCommunity m where m.circleid=?1 and m.delFlag=0 ")
    Long getCommentNum(String circleId);

    @Query(value = "select m from MedicalCircleCommunity m where m.id=?1 and m.doctorid=?2 and m.delFlag=0")
    MedicalCircleCommunity getCommunityWithUser(String commentId,String doctorId);


    @Query(value = "select m from MedicalCircleCommunity m where m.circleid=?1 and m.discusstime>?2 and m.delFlag=0 ")
    Page<MedicalCircleCommunity> findCommentsList(String cirlceId,Date flag,Pageable pageable);
}

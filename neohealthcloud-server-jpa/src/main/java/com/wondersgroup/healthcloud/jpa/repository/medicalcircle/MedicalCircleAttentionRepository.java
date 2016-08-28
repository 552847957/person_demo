package com.wondersgroup.healthcloud.jpa.repository.medicalcircle;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleAttention;

public interface MedicalCircleAttentionRepository extends JpaRepository<MedicalCircleAttention,String>{

    @Query(value = "select m from MedicalCircleAttention m where m.doctorid=?1 and m.concernedid=?2 and m.delFlag=0")
    MedicalCircleAttention findAttention(String attentionId,String followedId);

    @Query(value = "select count(1) from MedicalCircleAttention m where m.doctorid=?1 and m.delFlag=0")
    Long getAttentionNum(String doctorId);

    @Query(value = "select count(1) from MedicalCircleAttention m where m.concernedid=?1 and m.delFlag=0")
    Long getFansNum(String doctorId);

    @Query(value = "select m from MedicalCircleAttention m where m.doctorid=?1 and m.delFlag=0 and m.attentiontime<?2")
    Page<MedicalCircleAttention> findAttentionList(String doctorId,Date flag,Pageable pageable);

    @Query(value = "select m from MedicalCircleAttention m where m.concernedid=?1 and m.delFlag=0 and m.attentiontime<?2")
    Page<MedicalCircleAttention> findFansList(String doctorId,Date flag,Pageable pageable);

}

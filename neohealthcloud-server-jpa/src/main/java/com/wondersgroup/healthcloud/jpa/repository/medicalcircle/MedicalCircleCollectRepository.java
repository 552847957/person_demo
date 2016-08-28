package com.wondersgroup.healthcloud.jpa.repository.medicalcircle;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCollect;

public interface MedicalCircleCollectRepository extends JpaRepository<MedicalCircleCollect,String>{

    @Query(value = "select m from MedicalCircleCollect m where m.circleid=?1 and m.doctorid=?2 and m.type=?3 and m.delFlag=0")
    MedicalCircleCollect findCollectByDoctor(String circleId,String doctorId, int type);

    @Query(value = "select m from MedicalCircleCollect m where m.doctorid=?1 and m.delFlag=0 and m.collecttime<?2")
    Page<MedicalCircleCollect> findCollectList(String doctorId,Date flag,Pageable pageable);

    @Query(value = "select m from MedicalCircleCollect m where m.doctorid=?1 and type=?2 and m.delFlag=0 and m.collecttime<?3")
    Page<MedicalCircleCollect> findCollectListByType(String doctorId,int type, Date flag,Pageable pageable);
}

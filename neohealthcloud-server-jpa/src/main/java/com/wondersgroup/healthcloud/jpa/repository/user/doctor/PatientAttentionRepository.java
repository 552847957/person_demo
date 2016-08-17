package com.wondersgroup.healthcloud.jpa.repository.user.doctor;

import com.wondersgroup.healthcloud.jpa.entity.user.patientAttention.PatientAttention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by longshasha on 16/8/17.
 */
public interface PatientAttentionRepository extends JpaRepository<PatientAttention,String> {

    @Query("select a from PatientAttention a where a.registerId= ?1 and a.attentionId = ?2 ")
    PatientAttention findAttentionByUidAndDoctorId(String uid, String doctorid);
}

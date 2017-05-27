package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

public interface DoctorInterventionRepository extends JpaRepository<DoctorIntervention, String> {

    @Query(" select count(a) from DoctorIntervention a where a.doctorId = ?1 and a.delFlag ='0' ")
    int countHasInterventionByDoctorId(String doctorId);
}
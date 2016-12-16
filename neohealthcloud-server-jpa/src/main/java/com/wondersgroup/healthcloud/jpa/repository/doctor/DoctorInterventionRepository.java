package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

public interface DoctorInterventionRepository extends JpaRepository<DoctorIntervention, String> {
}
package com.wondersgroup.healthcloud.jpa.repository.medicalrecord;

import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by tanxueliang on 16/8/30.
 */
public interface MedicalCaseRepository extends JpaRepository<MedicalCase, String> {

    List<MedicalCase> findByDoctorId(String doctorId);

    List<MedicalCase> findByDoctorIdAndPatientnameLike(String doctorId, String patientname);

}

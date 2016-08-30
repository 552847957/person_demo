package com.wondersgroup.healthcloud.jpa.repository.medicalrecord;

import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by tanxueliang on 16/8/30.
 */
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {


    List<MedicalRecord> findByCaseId(String caseId);
}

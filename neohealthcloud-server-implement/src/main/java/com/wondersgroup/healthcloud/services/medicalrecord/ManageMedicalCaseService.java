package com.wondersgroup.healthcloud.services.medicalrecord;

import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalCase;
import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalRecord;

import java.util.List;

/**
 * Created by tanxueliang on 16/8/30.
 */
public interface ManageMedicalCaseService {

    public boolean addMedicalCase(MedicalCase medicalCase);

    public boolean updateMedicalCaseBasicInfo(MedicalCase medicalCase);

    public boolean addNewRecord(MedicalRecord record);

    public List<MedicalCase> getMedicalCaseByDoctorId(String uid);

    public MedicalCase getMedicalCaseByCaseId(String caseId);

    public List<MedicalRecord> getMedicalRecordByCaseId(String caseId);

    public MedicalRecord getSingleMedicalRecordByRecordId(String recordId);

    public List<MedicalCase> searchMedicalCaseByName(String name, String doctorId);
}

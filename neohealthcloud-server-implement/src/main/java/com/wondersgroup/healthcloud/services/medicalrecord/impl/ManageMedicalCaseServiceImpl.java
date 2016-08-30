package com.wondersgroup.healthcloud.services.medicalrecord.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalCase;
import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalRecord;
import com.wondersgroup.healthcloud.jpa.repository.medicalrecord.MedicalCaseRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalrecord.MedicalRecordRepository;
import com.wondersgroup.healthcloud.services.medicalrecord.ManageMedicalCaseService;

/**
 * Created by qiujun on 2015/9/4.
 */
@Service("manageMedicalCaseService")
public class ManageMedicalCaseServiceImpl implements ManageMedicalCaseService {

	@Autowired
	MedicalCaseRepository medicalCaseRepository;

	@Autowired
	MedicalRecordRepository medicalRecordRepository;

	@Override
	public boolean addMedicalCase(MedicalCase medicalCase) {
		return medicalCaseRepository.save(medicalCase) == null;
	}

	@Override
	public boolean updateMedicalCaseBasicInfo(MedicalCase medicalCase) {
		return medicalCaseRepository.save(medicalCase) == null;
	}

	@Override
	public boolean addNewRecord(MedicalRecord record) {
		return medicalRecordRepository.save(record) == null;

	}

	@Override
	public List<MedicalCase> getMedicalCaseByDoctorId(String doctorId) {
		return medicalCaseRepository.findByDoctorId(doctorId);
	}

	@Override
	public MedicalCase getMedicalCaseByCaseId(String caseId) {
		return medicalCaseRepository.findOne(caseId);
	}

	@Override
	public List<MedicalRecord> getMedicalRecordByCaseId(String caseId) {
		return medicalRecordRepository.findByCaseId(caseId);
	}

	@Override
	public MedicalRecord getSingleMedicalRecordByRecordId(String recordId) {
		return medicalRecordRepository.findOne(recordId);
	}

	@Override
	public List<MedicalCase> searchMedicalCaseByName(String name, String doctor) {

		if (StringUtils.isBlank(name)) {
			name = "";
		}

		return medicalCaseRepository.findByDoctorIdAndPatientnameLike(doctor, "%" + name + "%");
	}

	private List<MedicalCase> convertMedicalCase(List<Object> objs) {
		List<MedicalCase> cases = Lists.newArrayList();
		if (objs != null && !objs.isEmpty()) {
			for (Object obj : objs) {
				cases.add((MedicalCase) obj);
			}
		}
		return cases;
	}

	private List<MedicalRecord> convertMedicalRecord(List<Object> objs) {
		List<MedicalRecord> records = Lists.newArrayList();
		if (objs != null && !objs.isEmpty()) {
			for (Object obj : objs) {
				records.add((MedicalRecord) obj);
			}
		}
		return records;
	}
}

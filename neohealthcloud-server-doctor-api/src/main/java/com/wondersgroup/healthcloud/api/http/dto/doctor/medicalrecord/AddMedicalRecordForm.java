package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalrecord;

import lombok.Data;

@Data
public class AddMedicalRecordForm {

	private String caseId;
	private String actionType;
	private String remarks;
	private String imgUrls;

}

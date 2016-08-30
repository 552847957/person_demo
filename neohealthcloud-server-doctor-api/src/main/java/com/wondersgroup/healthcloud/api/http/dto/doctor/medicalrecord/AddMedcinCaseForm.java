package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalrecord;

import lombok.Data;

@Data
public class AddMedcinCaseForm {

	private String birthDay;
	private String doctorId;
	private String patientname;
	private String gender;
	private String mobile;
	private String medcinCard;
	private String actionType;
	private String remarks;
	private String imgUrls;

}

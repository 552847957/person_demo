package com.wondersgroup.healthcloud.jpa.entity.medicalrecord;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@Entity
@Table(name = "app_tb_medcin_record")
@JsonInclude(Include.NON_NULL)
public class MedicalRecord {

	@Id
	private String	recordId;
	private String	caseId;
	private String	actionName;
	private String	actionType;
	private String	remark;
	private String	imgs;
	private Date	addDate;

}

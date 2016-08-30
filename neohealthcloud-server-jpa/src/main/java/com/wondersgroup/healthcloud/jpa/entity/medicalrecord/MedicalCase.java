package com.wondersgroup.healthcloud.jpa.entity.medicalrecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.exceptions.Exceptions;

import lombok.Data;

/**
 * Created by qiujun on 2015/9/4.
 */

@Data
@Entity
@Table(name = "app_tb_medcin_case")
@JsonInclude(Include.NON_NULL)
public class MedicalCase {

	@Id
	private String	caseId;

	private String	doctorId;
	private String	patientname;
	private String	gendar;
	private String	birthDay;
	private String	mobile;
	private String	medicinCard;
	private String	lastUpdateDate;
	private String	age;

	public String getAge() {
		return DateUtils.getAgeByBirthday(parse(birthDay)) + "";
	}

	private Date parse(String source) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(birthDay);
		} catch (ParseException e) {
			throw Exceptions.unchecked(e);
		}
	}
}

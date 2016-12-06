package com.wondersgroup.healthcloud.api.http.dto.step;

import java.util.Date;

import com.wondersgroup.healthcloud.common.utils.DateUtils;

import lombok.Data;

@Data
public class StepHomeDto {

	private Integer restGold;

	private Integer awardGold;

	private boolean isGet = true;

	private String helpUrl;

	private String ruleUrl;

	public boolean isToday(Date recent) {
		Date date = new Date();
		String pattern = "yyyyMMdd";
		return DateUtils.format(date, pattern).equals(DateUtils.format(recent, pattern));
	}
	
	

}

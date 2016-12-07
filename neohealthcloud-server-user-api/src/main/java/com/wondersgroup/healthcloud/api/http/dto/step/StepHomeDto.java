package com.wondersgroup.healthcloud.api.http.dto.step;

import lombok.Data;

@Data
public class StepHomeDto {

	private Integer restGold;

	private Integer awardGold;

	private boolean hasGet = true;

	private String helpUrl;

	private String ruleUrl;
	
	private String inviteUrl;
	

}

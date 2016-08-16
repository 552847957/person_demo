package com.wondersgroup.healthcloud.services.user.dto.healthactivity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthActivityEvaluationAPIEntity {
	private String level ;// '评价等级',
	 private String content ;// '评价内容',
	 private String time;// '评价时间',
	public HealthActivityEvaluationAPIEntity(){
		
	}
	public HealthActivityEvaluationAPIEntity(HealthActivityDetail detail){
		this.level = detail.getEvaluate();
		this.content = detail.getEvalatememo();
		this.time = detail.getEvaluatetime();
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
	
	 
}

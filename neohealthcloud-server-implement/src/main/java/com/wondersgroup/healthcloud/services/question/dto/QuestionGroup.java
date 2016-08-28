package com.wondersgroup.healthcloud.services.question.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuestionGroup {
	private String id;
	private Boolean isReply;
	private String avatar;//医生头像
	private String doctorName;
	private String doctorId;
	private String title;
	private Integer status;
	private List<QuestionComment> questionComment;
	public QuestionGroup(Map<String,Object> map) {
		this.id = (String) map.get("id");
		this.avatar = map.containsKey("avatar") ? (String) map.get("avatar") : null;
		this.doctorName = map.containsKey("name") ? (String) map.get("name") : null;
		this.doctorId = map.containsKey("doctorId") ? map.get("doctorId").toString() : null;
		this.title = map.containsKey("duty_name") ? (String) map.get("duty_name") : null;
		this.status = map.containsKey("status") ? (int) map.get("status") : null;
	}
}

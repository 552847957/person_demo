package com.wondersgroup.healthcloud.services.question.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionInfoForm {
	
	private String id;
	private String content;
	private String doctorName;
	private Integer isRead;
	private Integer status;
	private String date;
	private Integer contentCount;
	public QuestionInfoForm(Map<String,Object> map) {
		this.id = (String) map.get("id");
		this.status = map.containsKey("status") ? (int) map.get("status") : null;
		this.content = map.containsKey("content") ? (String) map.get("content") : null;
		this.doctorName = map.containsKey("name") ? (String) map.get("name") : null;
		this.date = map.containsKey("date") ? (String) map.get("date") : "";
		this.contentCount = map.containsKey("comment_count") ? (int) map.get("comment_count") : null;
		if (map.containsKey("isNoRead")){
			this.isRead =  map.get("isNoRead").toString().equals("1") ? 0 : 1;
		}else if (map.containsKey("isRead")){
			this.isRead = map.get("isRead").toString().equals("1") ? 1 : 0;
		}else {
			this.isRead = null;
		}
	}
	
}

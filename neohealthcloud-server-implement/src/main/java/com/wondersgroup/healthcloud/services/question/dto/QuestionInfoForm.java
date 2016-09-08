package com.wondersgroup.healthcloud.services.question.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
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
	private Integer hasAt=1;
	private String assign_answer_id;
	public QuestionInfoForm(Map<String,Object> map) {
		this.id = (String) map.get("id");
		this.status = map.containsKey("status") ? (int) map.get("status") : null;
		this.content = map.containsKey("content") ? (String) map.get("content") : null;
		this.doctorName = map.containsKey("name") ? (String) map.get("name") : null;
		this.contentCount = map.containsKey("comment_count") ? (int) map.get("comment_count") : null;
		this.isRead =map.containsKey("isNoRead") ?(int) map.get("isNoRead"):null;
		if((map.containsKey("status")&&status==3)||status==null){
			this.date = map.containsKey("date") ?((String) map.get("date")): "";
		}else{
			if ( !map.containsKey("date2")){
				this.date = map.containsKey("date") ? ((String) map.get("date")): "";
			}else{
				this.date = map.containsKey("date2") ? ((String) map.get("date2")): "";
			}
		}
		if( map.containsKey("assign_answer_id")&& !StringUtils.isEmpty(map.get("assign_answer_id"))){
			this.hasAt=0;
		}
	}
	
}

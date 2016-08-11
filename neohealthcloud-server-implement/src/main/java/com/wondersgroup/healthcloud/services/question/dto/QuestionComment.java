package com.wondersgroup.healthcloud.services.question.dto;

import lombok.Data;

import java.util.Map;

@Data
public class QuestionComment {
		
	private int userReply;
	private String content;
	private String contentImgs;
	private String date;
	
	public QuestionComment(Map<String,Object> map) {
		this.userReply = (int) map.get("is_user_reply");
		this.content = (String) map.get("content");
		this.contentImgs = (String) map.get("content_imgs");
		this.date = (String) map.get("date");
	}

}

package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDetail {
	
	private String sex;
	private int age;
	private int status;//1:进行中(含有未读信息的),2:进行中(不含有未读信息的),3：已关闭的
	private String content;
	private String date;
	private String contentImgs;
	private List<QuestionGroup> group;

	public QuestionDetail(Question question) {
		this.sex = question.getSex() == 1 ? "男" : "女";
		this.age = question.getAge();
		this.status = question.getStatus();
		this.content = question.getContent();
		this.date = (new SimpleDateFormat("MM-dd HH:mm")).format(question.getCreateTime());
		this.contentImgs = question.getContentImgs();
	}
}

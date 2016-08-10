package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorQuestionDetail {

    private String sax;
    private int age;
    private int status;//1:进行中(含有未读信息的),2:进行中(不含有未读信息的),3：已关闭的
    private String content;
    private String date;
    private String contentImgs;
    private List<QuestionGroup> group;

    public DoctorQuestionDetail(Question question) {
        this.sax = question.getSax() == 1 ? "男" : "女";
        this.age = question.getAge();
        this.status = question.getStatus();
        this.content = question.getContent();
        this.contentImgs = question.getContentImgs();
        this.date = (new SimpleDateFormat("MM-dd HH:mm")).format(question.getCreateTime());
    }

    public List<QuestionGroup> getGroup() {
        return group;
    }
    public void setGroup(List<QuestionGroup> group) {
        this.group = group;
    }
}

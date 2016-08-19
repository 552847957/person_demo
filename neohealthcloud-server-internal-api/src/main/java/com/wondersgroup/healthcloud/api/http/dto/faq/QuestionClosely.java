package com.wondersgroup.healthcloud.api.http.dto.faq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.utils.DateFormatter;

/**
 * Created by longshasha on 16/8/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionClosely {

    private String id;

    @JsonProperty("q_id")
    private String qId;

    @JsonProperty("q_pid")
    private String qPid;

    @JsonProperty("asker_name")
    private String askerName;
    private Integer gender;
    private Integer age;

    @JsonProperty("question_content")
    private String questionContent;

    @JsonProperty("ask_time")
    private String askTime;

    @JsonProperty("answer_content")
    private String answerContent;

    @JsonProperty("answer_time")
    private String answerTime;

    public QuestionClosely(Faq faqClosely) {
        this.id = faqClosely.getId();
        this.qId = faqClosely.getId();
        this.qPid = faqClosely.getQPid();
        this.askerName = faqClosely.getAskerName();
        this.gender = faqClosely.getGender();
        this.age = faqClosely.getAge();
        this.questionContent = faqClosely.getAskContent();
        this.askTime = DateFormatter.dateTimeFormat(faqClosely.getAskDate());
        this.answerContent = faqClosely.getAnswerContent()==null?"":faqClosely.getAnswerContent();
        this.answerTime = "";
        if(faqClosely.getAnswerDate()!=null){
            this.answerTime = DateFormatter.dateTimeFormat(faqClosely.getAnswerDate());
        }

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getqPid() {
        return qPid;
    }

    public void setqPid(String qPid) {
        this.qPid = qPid;
    }

    public String getAskerName() {
        return askerName;
    }

    public void setAskerName(String askerName) {
        this.askerName = askerName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }
}

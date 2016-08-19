package com.wondersgroup.healthcloud.api.http.dto.faq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.utils.DateFormatter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorAnswer {


    private String id;

    @JsonProperty("q_id")
    private String qId;

    @JsonProperty("doctor_id")
    private String doctorId;

    @JsonProperty("doctor_avatar")
    private String doctorAvatar;

    @JsonProperty("doctor_name")
    private String doctorName;

    @JsonProperty("duty_name")
    private String dutyName;

    @JsonProperty("answer_content")
    private String answerContent;

    @JsonProperty("answer_time")
    private String answerTime;

    @JsonProperty("question_closely")
    private List<QuestionClosely> questionCloselies;

    public DoctorAnswer(Faq faq) {
    }

    public DoctorAnswer(Map<String, Object> faq) {
        this.id = faq.get("id")==null?"":faq.get("id").toString();
        this.qId = faq.get("qId")==null?"":faq.get("qId").toString();
        this.doctorId = faq.get("doctorId")==null?"":faq.get("doctorId").toString();
        this.doctorAvatar = faq.get("doctorAvatar")==null?"":faq.get("doctorAvatar").toString();
        this.doctorName = faq.get("doctorName")==null?"":faq.get("doctorName").toString();
        this.dutyName = faq.get("dutyName")==null?"":faq.get("dutyName").toString();
        this.answerContent = faq.get("answerContent")==null?"":faq.get("answerContent").toString();
        Date answerDate = (Date)faq.get("answerDate");
        if(answerDate !=null){
            this.answerTime = DateFormatter.questionDateFormat(answerDate);
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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorAvatar() {
        return doctorAvatar;
    }

    public void setDoctorAvatar(String doctorAvatar) {
        this.doctorAvatar = doctorAvatar;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
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

    public List<QuestionClosely> getQuestionCloselies() {
        return questionCloselies;
    }

    public void setQuestionCloselies(List<QuestionClosely> questionCloselies) {
        this.questionCloselies = questionCloselies;
    }

}

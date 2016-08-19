package com.wondersgroup.healthcloud.api.http.dto.faq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/15.
 */

/**
 * 问答集锦对象类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FaqDTO {

    private String id;

    @JsonProperty("asker_name")
    private String askerName;
    private Integer gender;
    private Integer age;

    @JsonProperty("ask_content")
    private String askContent;

    @JsonProperty("comment_count")
    private Integer commentCount;

    @JsonProperty("asker_time")
    private String askTime;

    @JsonProperty("doctor_answers")
    private List<DoctorAnswer> doctorAnswers;

    public FaqDTO(Faq faq) {
        this.id = faq.getId();
        this.askerName = faq.getAskerName();
        this.gender = faq.getGender();
        this.age = faq.getAge();
        this.askContent = faq.getAskContent();
        this.askTime = DateFormatter.questionDateFormat(faq.getAskDate());
    }

    public FaqDTO(Map<String, Object> faq) {
        this.id = faq.get("id")==null?"":faq.get("id").toString();
        this.askerName = faq.get("askerName")==null?"":faq.get("askerName").toString();
        String gender = faq.get("gender")==null?"":faq.get("gender").toString();
        if(StringUtils.isNotBlank(gender)){
            this.gender = Integer.valueOf(gender);
        }

        String age = faq.get("age")==null?"":faq.get("age").toString();
        if(StringUtils.isNotBlank(age)){
            this.age = Integer.valueOf(age);
        }
        this.askContent = faq.get("askContent")==null?"":faq.get("askContent").toString();
        Date askDate = (Date)faq.get("askDate");
        if(askDate!=null){
            this.askTime = DateFormatter.questionDateFormat(askDate);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAskerName() {
        return askerName;
    }

    public void setAskerName(String askerName) {
        this.askerName = askerName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAskContent() {
        return askContent;
    }

    public void setAskContent(String askContent) {
        this.askContent = askContent;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public List<DoctorAnswer> getDoctorAnswers() {
        return doctorAnswers;
    }

    public void setDoctorAnswers(List<DoctorAnswer> doctorAnswers) {
        this.doctorAnswers = doctorAnswers;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
}

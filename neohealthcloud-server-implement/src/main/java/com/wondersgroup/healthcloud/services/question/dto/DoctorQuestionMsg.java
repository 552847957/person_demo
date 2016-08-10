package com.wondersgroup.healthcloud.services.question.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorQuestionMsg {

    private String id;
    private String content;
    private String date;
    private int type=1;//1:新问题，2:新回复
    public DoctorQuestionMsg(Map<String,Object> map) {
        this.id = (String) map.get("id");
        this.content = map.containsKey("content") ? (String) map.get("content") : null;
        this.date = map.containsKey("date") ? (String) map.get("date") : "";
    }
}

package com.wondersgroup.healthcloud.services.question.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionInfoFormNew {

    private String id;
    private String content;
    private String doctorName;
    private Integer isRead;
    private Integer status;
    private String sex;
    private String date;
    private Integer contentCount;
    private Integer hasAt = 1;
    private String assign_answer_id;
    public Date sortDate;

    public QuestionInfoFormNew(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.status = map.containsKey("status") ? Integer.parseInt(String.valueOf(map.get("status"))) : null;
        this.sex = map.containsKey("sex") ? ((int) map.get("sex") == 1 ? "男" : "女") : "男";
        this.content = map.containsKey("content") ? (String) map.get("content") : null;
        this.doctorName = map.containsKey("name") ? (String) map.get("name") : null;
        this.contentCount = map.containsKey("comment_count") ? (int) map.get("comment_count") : null;
        this.isRead = map.containsKey("isNoRead") ? (int) map.get("isNoRead") : null;
        this.assign_answer_id = map.containsKey("assign_answer_id") ? String.valueOf(map.get("assign_answer_id")) : null;
        this.sortDate = map.containsKey("sortDate") ? (Date) map.get("sortDate") : null;

        if ((map.containsKey("status") && status == 3) || status == null) {
            this.date = map.containsKey("date") ? ((String) map.get("date")) : "";
        } else {
            if (!map.containsKey("date2")) {
                this.date = map.containsKey("date") ? ((String) map.get("date")) : "";
            } else {
                this.date = map.containsKey("date2") ? ((String) map.get("date2")) : "";
            }
        }

    }



   public void setMySortDate(Date sortDate){
       if (null != sortDate) {
           this.sortDate = sortDate;
           SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           this.date = formatter.format(sortDate);
       }
   }
}

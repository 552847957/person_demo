package com.wondersgroup.healthcloud.services.user.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by zhangzhixiu on 15/9/5.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoodAPIEntity {

    @JsonProperty("has_recent")
    private Boolean hasRecent;
    private Integer mood;
    @JsonProperty("date_time_format")
    private String dateTimeFormat;
    private FamilyMemberAPIEntity info;
//    private List<MoodHistoryAPIEntity> history;

    public Boolean getHasRecent() {
        return hasRecent;
    }

    public void setHasRecent(Boolean hasRecent) {
        this.hasRecent = hasRecent;
    }

    public Integer getMood() {
        return mood;
    }

    public void setMood(Integer mood) {
        this.mood = mood;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public FamilyMemberAPIEntity getInfo() {
        return info;
    }

    public void setInfo(FamilyMemberAPIEntity info) {
        this.info = info;
    }

//    public List<MoodHistoryAPIEntity> getHistory() {
//        return history;
//    }
//
//    public void setHistory(List<MoodHistoryAPIEntity> history) {
//        this.history = history;
//    }
}

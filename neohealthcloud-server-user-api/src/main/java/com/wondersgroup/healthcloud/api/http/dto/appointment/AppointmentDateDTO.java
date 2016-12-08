package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.utils.DateFormatter;

import java.util.Date;

/**
 * Created by longshasha on 16/12/7.
 * 按科室下面-按照日期预约 日期列表
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDateDTO {

    public String date;

    public String week;

    @JsonProperty("schedule_date")
    public String scheduleDate;

    public AppointmentDateDTO(Date currentDate) {
        this.date = DateFormatter.monthDayFormat(currentDate);
        this.week = DateUtils.getWeekOfDate(currentDate);
        this.scheduleDate = DateFormatter.dateFormat(currentDate);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}



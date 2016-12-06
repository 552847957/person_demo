package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctorSchedule;
import com.wondersgroup.healthcloud.utils.DateFormatter;

/**
 * Created by longshasha on 16/5/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorScheduleDetailDTO {
    private String id;
    
    @JsonProperty("schedule_date")
    private String scheduleDate;//2016年7月15号 预约日期

    @JsonProperty("time_range")
    private String timeRange;//上下午标志  周一 上午

    @JsonProperty("can_reserve")
    private Boolean canReserve;//

    private String fee;//费用

    private String time;//时间段

    public DoctorScheduleDetailDTO(AppointmentDoctorSchedule schedule) {
        this.id = schedule.getId();
        this.scheduleDate = DateFormatter.scheduleDateFormat(schedule.getScheduleDate());
        this.timeRange = getTimeRangBySchedule(schedule);
        this.canReserve = true;
        this.fee = schedule.getVisitCost();
        this.time = schedule.getStartTime() + "-" + schedule.getEndTime();
    }


    private String getTimeRangBySchedule(AppointmentDoctorSchedule schedule) {
        String result = "";
        if(schedule.getScheduleDate() !=null){
            result = DateUtils.getWeekOfDate(schedule.getScheduleDate());
        }
        result += getTimeRangeStr(schedule.getTimeRange());
        return result;
    }

    private String getTimeRangeStr(String timeRangeCode) {
        String timeRangeStr = "";
        if("1".equals(timeRangeCode)){
            timeRangeStr = "上午";
        }else if("2".equals(timeRangeCode)){
            timeRangeStr = "下午";
        }else if("3".equals(timeRangeCode)){
            timeRangeStr = "晚上";
        }
        return timeRangeStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public Boolean getCanReserve() {
        return canReserve;
    }

    public void setCanReserve(Boolean canReserve) {
        this.canReserve = canReserve;
    }


    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

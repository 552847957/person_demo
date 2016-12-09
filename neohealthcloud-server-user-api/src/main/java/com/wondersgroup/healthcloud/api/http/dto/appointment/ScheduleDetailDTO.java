package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.services.appointment.dto.ScheduleDto;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by longshasha on 16/5/21.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDetailDTO {

    /**
     * 排班Id
     */
    public String id;

    public String name;

    /**
     * 医生头像
     */
    public String avatar;

    /**
     *医生职称
     */
    @JsonProperty("duty_name")
    public String dutyName;

    public String specialty;

    @JsonProperty("reservation_num")
    public String reservationNum;

    /**
     * 预约状态- 0: 无排班 1: 可预约 2: 约满
     */
    @JsonProperty("reservation_status")
    public String reservationStatus;

    /**
     * 1:医生 2:科室
     */
    public String type;
    
    @JsonProperty("schedule_date")
    public String scheduleDate;//2016年7月15号 预约日期

    /**
     * 周
     */
    public String week;

    /**
     * 时间段 13:00-16:00
     */
    public String time;//时间段

    /**
     * 费用 45.00
     */
    public String fee;//费用

    /**
     * 门诊类型
     */
    @JsonProperty("visit_level")
    private String visitLevelCode;


    public ScheduleDetailDTO(){

    }

    public ScheduleDetailDTO(ScheduleDto schedule) {
        if(schedule!=null){
            this.id = schedule.getId();
            this.name = schedule.getName()==null?"":schedule.getName();
            this.avatar = schedule.getAvatar()==null?"":schedule.getAvatar();
            this.dutyName = schedule.getDutyName()==null?"":schedule.getDutyName();
            this.specialty = schedule.getSpecialty()==null?"":schedule.getSpecialty();
            this.fee = schedule.getVisitCost()==null?"":schedule.getVisitCost();
            this.reservationNum = String.valueOf(schedule.getReservationNum()==null?0:schedule.getReservationNum());
            this.type = schedule.getType();
            this.scheduleDate = DateFormatter.dateFormat(schedule.getScheduleDate());
            this.week = DateUtils.getWeekOfDate(schedule.getScheduleDate());
            this.time = DateFormatter.hourDateFormat(schedule.getStartTime())+"-"+DateFormatter.hourDateFormat(schedule.getEndTime());
            this.fee = schedule.getVisitCost();

            this.reservationStatus = "1";
            int reserveOrderNum = schedule.getReserveOrderNum()==null?0:schedule.getReserveOrderNum();
            if(reserveOrderNum==0){
                this.reservationStatus = "2";
            }
            if(StringUtils.isBlank(schedule.getVisitLevelCode())){
                this.visitLevelCode = "其他";
            }else if("1".equals(schedule.getVisitLevelCode())){
                this.visitLevelCode = "专家门诊";
            }else if("2".equals(schedule.getVisitLevelCode())){
                this.visitLevelCode = "专病门诊";
            }else if("3".equals(schedule.getVisitLevelCode())){
                this.visitLevelCode = "普通门诊";
            }

        }

    }

}

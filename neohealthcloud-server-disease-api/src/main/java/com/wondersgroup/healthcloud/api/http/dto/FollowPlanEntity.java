package com.wondersgroup.healthcloud.api.http.dto;

import java.util.Date;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.wondersgroup.healthcloud.services.diabetes.dto.FollowPlanDTO;

/**
 * 
 * @author zhongshuqing
 *
 */
@Data
public class FollowPlanEntity {
    
    private String followDate;//随访日期
    private String hospitalName;//医疗机构名称
    private String doctorName;//随访医生
    private String followDateInterval;//随访日期间隔
    private String followCrowedType;//随访人群种类
    private boolean isOverdueFlag;//是否过期标示
    public FollowPlanEntity(FollowPlanDTO dto) {
        this.followDate = null == dto.getFollowDate() ? null : new DateTime(dto.getFollowDate()).toString("yyyy-MM-dd");
        this.doctorName = dto.getDoctorName();
        this.hospitalName = dto.getHospitalName();
        this.followCrowedType = dto.getFollowCrowedType();
        if(StringUtils.isNotBlank(followDate)){
            int dateInterval = differentDaysByMillisecond(new Date(),dto.getFollowDate());
            if(dateInterval>=151&&dateInterval<=180){
                this.followDateInterval="6个月之后";
            }else if(dateInterval>=121&&dateInterval<=150){
                this.followDateInterval="5个月之后";
            }else if(dateInterval>=91&&dateInterval<=120){
                this.followDateInterval="4个月之后";
            }else if(dateInterval>=61&&dateInterval<=90){
                this.followDateInterval="3个月之后";
            }else if(dateInterval>=31&&dateInterval<=60){
                this.followDateInterval="2个月之后";
            }else if(dateInterval>=1&&dateInterval<=30){
                this.followDateInterval=dateInterval+"天之后";
            }else if(dateInterval==0){
                this.followDateInterval="当天";
            }else{
                this.followDateInterval="随访已经过期";
                this.isOverdueFlag=true;
            }
        }
    }
    
    /**
    * 通过时间秒毫秒数判断两个时间的间隔
    * @param date1
    * @param date2
    * @return
    */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
    int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
    return days;
    }
}

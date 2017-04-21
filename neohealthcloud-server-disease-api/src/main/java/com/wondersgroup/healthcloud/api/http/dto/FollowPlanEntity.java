package com.wondersgroup.healthcloud.api.http.dto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    //private String followDateInterval;//随访日期间隔
    private String followCrowedType;//随访人群种类
    private boolean isOverdueFlag;//是否过期标示
    public FollowPlanEntity(FollowPlanDTO dto) {
        this.followDate = null == dto.getFollowDate() ? null : new DateTime(dto.getFollowDate()).toString("yyyy-MM-dd");
        int year = new DateTime(dto.getFollowDate()).getYear();
        int month = new DateTime(dto.getFollowDate()).getMonthOfYear(); 
        //int day = new DateTime(dto.getFollowDate()).getDayOfMonth(); 
        
        this.doctorName = dto.getDoctorName();
        this.hospitalName = dto.getHospitalName();
        this.followCrowedType = dto.getFollowCrowedType();
        if(StringUtils.isNotBlank(followDate)&&"3".equals(followCrowedType)){
                if(getQuarterByMonth(month)==9){
                    int i = differentDaysByMillisecond(new Date(),stringToDate(year+"-12-31"));
                    this.followDate=followDate+" 到 "+year+"-12-31";
                    if(i<0){
                        this.isOverdueFlag=true;   
                    }
                }else if(getQuarterByMonth(month)==0){
                    int i = differentDaysByMillisecond(new Date(),stringToDate(year+"-03-31"));
                    this.followDate=followDate+" 到 "+year+"-03-31";
                    if(i<0){
                        this.isOverdueFlag=true;   
                    }
                }else if(getQuarterByMonth(month)==3){
                    int i = differentDaysByMillisecond(new Date(),stringToDate(year+"-06-30"));
                    this.followDate=followDate+" 到 "+year+"-06-30";
                    if(i<0){
                        this.isOverdueFlag=true;   
                    }
                }else{
                    this.followDate=followDate+" 到 "+year+"-09-30"; 
                    int i = differentDaysByMillisecond(new Date(),stringToDate(year+"-09-30"));
                    if(i<0){
                        this.isOverdueFlag=true;   
                    }
                }
          
        }else if(StringUtils.isNotBlank(followDate)&&"1".equals(followCrowedType)){
                if(getHighRishByMonth(month)==0){
                    int i = differentDaysByMillisecond(new Date(),stringToDate(year+"-09-30"));
                    this.followDate=followDate+" 到 "+year+"-09-30";
                    if(i<0){
                        this.isOverdueFlag=true;   
                    }
                }else{
                    int i = differentDaysByMillisecond(new Date(),stringToDate(year+"-03-31"));
                    //this.followDate=(year-1)+"-10-01 到 "+followDate;
                    if(year<new DateTime(new Date()).getYear()){
                        this.followDate=followDate+" 到 "+(year+1)+"-03-31";
                    }else{
                        this.followDate=followDate+" 到 "+year+"-03-31";
                    }
                    if(i<0){
                        this.isOverdueFlag=true;   
                    }
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
    /**
     * String 转 Date
     * @param time
     * @return
     */
    public static Date stringToDate(String time) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");//日期格式
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * 季度一年四季， 第一季度：1月-3月， 第二季度：4月-6月， 第三季度：7月-9月， 第四季度：10月-12月
     * 
     * @param month
     *            需要查找的月份0-11,Java中的月份是从0开始计算的.
     * @return 当前季度开始的月份.分别是0=1月,3=4月,6=7月,9=10月
     */
    public static int getQuarterByMonth(int month) {
            int months[] = { 0, 3, 6, 9 };
            if (month >= 1 && month <= 3) // 1-3月;
                    return months[0];
            else if (month >= 4 && month <= 6) // 4-6月;
                    return months[1];
            else if (month >= 7 && month <= 9) // 7-9月;
                    return months[2];
            else
                    // 10-12月;
                    return months[3];
    }
    /**
     * 高危人群随访日期月
     * @param month
     * @return
     */
    public static int getHighRishByMonth(int month){
        int months[] = { 0, 1};
        if(month >= 4 && month <= 9){//3月到10月
           return   months[0];
        }else{
           return   months[1]; //4月到9月
        }
    }
}

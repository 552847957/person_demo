package com.wondersgroup.healthcloud.services.remind.dto;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.Data;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.remind.Remind;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;

/**
 * 
 * @author zhongshuqing  2017.05.09
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindForHomeDTO {
    

    private String id;//id
    
    private String name;//药品名称
    
    private String tarGetUrl;//目标链接
    
    private String remindTime;//提醒时间
    
    private String msg;
    
    public RemindForHomeDTO(){
        
    }
    public RemindForHomeDTO(Remind remind, List<RemindItem> remindItems, List<RemindTime> remindTimes) {
        this.id=remind.getId();
        if(CollectionUtils.isNotEmpty(remindItems)){
            String name=remindItems.get(0).getName();
            if(name.length()>5){
                this.name=name.substring(0, 5)+"...";
            }else{
                this.name=name;
            }
        }
        long nowTime = 0;
        if(CollectionUtils.isNotEmpty(remindTimes)){
            if(remindTimes.size()==1){
                this.remindTime=remindTimes.get(0).getRemindTime()+"";
            }else{
                List<Long> midList = new ArrayList<Long>();
                List<Long> maxList = new ArrayList<Long>();
                List<Long> minList = new ArrayList<Long>();
                nowTime = stringToDate(dateToString(new Date())).getTime();
                for (RemindTime rt : remindTimes) {
                    Time remidTime = rt.getRemindTime();
                    long time = remidTime.getTime();//转成毫秒
                    if(nowTime>time){
                        maxList.add(time);
                    }else if(nowTime<time){
                        minList.add(time);
                    }else{
                        midList.add(time);
                    }
                }
                if(CollectionUtils.isNotEmpty(maxList)&&CollectionUtils.isNotEmpty(minList)){
                    maxList.addAll(minList);
                    maxList.add(nowTime);
                    Collections.sort(maxList); 
                    for(int i=0;i<maxList.size();i++){
                       if(nowTime==maxList.get(i)){
                          this.remindTime=dateToString(new Date(maxList.get(i+1)));
                          break;
                       } 
                    }
                }else if(CollectionUtils.isNotEmpty(minList)){
                    Long min = Collections.min(minList);
                    this.remindTime=dateToString(new Date(min));
                }else if(CollectionUtils.isNotEmpty(maxList)){
                    Long min = Collections.min(maxList);
                    this.remindTime=dateToString(new Date(min));
                }else{
                    Long mid = Collections.max(midList);
                    this.remindTime=dateToString(new Date(mid));
                }
            }
        }
    }
    /**
     * String 转 Date
     * @param time
     * @return
     */
    public static Date stringToDate(String time) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss");//日期格式
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * Date 转 String
     * @param date
     * @return
     */
    public static String dateToString(Date date){
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }
}

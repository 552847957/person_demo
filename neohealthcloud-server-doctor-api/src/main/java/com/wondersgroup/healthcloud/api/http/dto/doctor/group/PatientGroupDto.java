package com.wondersgroup.healthcloud.api.http.dto.doctor.group;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.group.PatientGroup;

/**
 * 
 * @author zhongshuqing 
 *      患者分组管理
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientGroupDto {
    
    private Integer id;
    
    private String name;//分组名称
    
    private String createDate;
    
    private String sort;
    public PatientGroupDto(){
        
    }
    
    public PatientGroupDto(PatientGroup p){
        this.id=p.getId();
        this.name=p.getName();
        this.sort=p.getRank()+"";
        this.createDate=dateToString(p.getCreateTime());
    }
    public static String dateToString(Date date) { 
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //format = DateFormat.getDateInstance(DateFormat.MEDIUM);  
        return format.format(date);
    }
}

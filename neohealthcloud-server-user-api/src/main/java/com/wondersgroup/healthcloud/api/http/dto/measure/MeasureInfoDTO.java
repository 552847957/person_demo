package com.wondersgroup.healthcloud.api.http.dto.measure;

import lombok.Data;

@Data
public class MeasureInfoDTO {

    private String title;
    private String name;
    private String desc;
    private String date;
    private String value;
    private String flag;
    
    public MeasureInfoDTO() {
        
    }
    
    public MeasureInfoDTO(String title, String name, String date, String value) {
        this.title = title;
        this.name = name;
        this.date = date;
        this.value = value;
    }
    
    
    public MeasureInfoDTO(String title, String name, String desc, String date, String value, String flag) {
        this.title = title;
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.value = value;
        this.flag = flag;
    }
    
}

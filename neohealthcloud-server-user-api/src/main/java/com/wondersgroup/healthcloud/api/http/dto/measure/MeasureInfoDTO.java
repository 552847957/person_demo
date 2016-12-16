package com.wondersgroup.healthcloud.api.http.dto.measure;

import lombok.Data;

@Data
public class MeasureInfoDTO {

    private String name;
    private String date;
    private String value;
    private String flag;
    
    public MeasureInfoDTO() {
        
    }
    
    public MeasureInfoDTO(String name, String date, String value) {
        this.name = name;
        this.date = date;
        this.value = value;
    }
    
    
    public MeasureInfoDTO(String name, String date, String value, String flag) {
        this.name = name;
        this.date = date;
        this.value = value;
        this.flag = flag;
    }
    
}

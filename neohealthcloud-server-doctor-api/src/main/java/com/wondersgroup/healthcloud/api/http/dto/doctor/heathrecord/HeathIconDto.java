package com.wondersgroup.healthcloud.api.http.dto.doctor.heathrecord;

import lombok.Data;

import java.util.List;

/**
 * Created by sunhaidi on 2017/6/1.
 */
@Data
public class HeathIconDto {
    public String name;
    public String url;
    public String imgUrl;
    public int isNew;

    public HeathIconDto(){

    }

    public HeathIconDto(String name, String url, String imgUrl){
        this.name = name;
        this.url = url;
        this.imgUrl = imgUrl;
    }



}

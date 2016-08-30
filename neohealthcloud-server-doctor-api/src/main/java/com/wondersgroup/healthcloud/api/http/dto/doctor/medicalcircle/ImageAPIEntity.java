package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ImageAPIEntity {
    private Float   ratio; //长宽比
    private String  url;   //标准图
    private String  type;  //图片类型
    private String  thumb; //缩略图
    private Integer height;
    private Integer width;

}

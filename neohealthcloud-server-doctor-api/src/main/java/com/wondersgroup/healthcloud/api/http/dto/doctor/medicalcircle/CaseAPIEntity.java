package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CaseAPIEntity {
    private String title;//标题
    private String content;//内容
    private List<ImageAPIEntity> images;

}

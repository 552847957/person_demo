package com.wondersgroup.healthcloud.services.home.dto.functionIcons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 主要功能区
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FunctionIconsDTO {

    private String imgUrl;
    private String mainTitle;
    private String hoplink;
    private String subTitle;
    

}

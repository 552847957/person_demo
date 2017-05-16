package com.wondersgroup.healthcloud.services.home.dto.specialService;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 特色服务
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecialServiceDTO {
    private String id;
    private String imgUrl;
    private String mainTitle;
    private String subTitle;
    private String hoplink;
    private Integer loginOrRealName;

}

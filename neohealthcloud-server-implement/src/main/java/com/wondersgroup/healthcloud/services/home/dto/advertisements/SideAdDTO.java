package com.wondersgroup.healthcloud.services.home.dto.advertisements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 侧边广告
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SideAdDTO {
    private String position;
    private String imgUrl;
    private String hoplink;


}

package com.wondersgroup.healthcloud.services.home.dto.advertisements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 中央区域广告
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CenterAdDTO {
    private String imgUrl;
    private String hoplink;
}

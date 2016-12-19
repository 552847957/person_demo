package com.wondersgroup.healthcloud.services.home.dto.cloudTopLine;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 云头条-message
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudTopLineMsgDTO {
    private String type;
    private String id;
    private String title;
    private String jumpUrl;

}

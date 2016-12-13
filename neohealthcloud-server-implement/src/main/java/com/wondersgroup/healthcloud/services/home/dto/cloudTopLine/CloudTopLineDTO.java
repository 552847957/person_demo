package com.wondersgroup.healthcloud.services.home.dto.cloudTopLine;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * 云头条
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudTopLineDTO {
    private String iconUrl;
    private List<CloudTopLineMsgDTO> message;
}

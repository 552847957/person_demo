package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * 个人健康
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserHealthJKGLDTO {
    private String healthStatus;
    private String mainTitle;
    private String  subTitle;
    private String headPhoto;
    private List<UserHealthItemDTO> exceptionItems;
    private List<UserHealthItemDTO> healthItems;

}

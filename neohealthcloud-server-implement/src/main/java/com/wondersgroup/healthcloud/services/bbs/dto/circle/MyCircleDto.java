package com.wondersgroup.healthcloud.services.bbs.dto.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 我的圈子
 * Created by limenghua on 2016/8/11.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyCircleDto {

    private Integer id;
    private String name;
    private String icon;
    private Integer rank;//排序
}

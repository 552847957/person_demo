package com.wondersgroup.healthcloud.services.bbs.dto.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Created by limenghua on 2016/8/12.
 *
 * @author limenghua
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CircleListDto {
    private Integer id;
    private String name;
    private String description;
    private String icon;
    private Integer rank;// 排序权重
    private Integer ifAttent = 0;// 是否关注，0：未关注；1：已关注
    private boolean isForbidden;

}

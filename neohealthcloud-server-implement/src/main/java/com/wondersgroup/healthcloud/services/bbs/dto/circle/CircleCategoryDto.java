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
public class CircleCategoryDto {
    private Integer id;

    private String name;

    private Integer rank;//排序

    private String delflag;

    private String circleNames;// 下属圈子名称
}

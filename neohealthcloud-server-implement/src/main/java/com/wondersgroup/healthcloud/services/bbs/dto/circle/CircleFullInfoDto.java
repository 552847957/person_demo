package com.wondersgroup.healthcloud.services.bbs.dto.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.services.bbs.dto.AdminAccountDto;
import lombok.Data;

import java.util.List;

/**
 * Created by limenghua on 2016/8/16.
 *
 * @author limenghua
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CircleFullInfoDto {
    private Integer id;
    private String name;
    private String icon;// 圈子图标
    private String description;//圈子描述
    private Integer ifAttent = 0;// 是否关注，0：未关注；1：已关注
    private Integer categoryId;// 圈子分类id
    private String categoryName;// 圈子分类名称
    private String topicCount;// 如：1.2万
    private String circleManCount;// 圈子人数
    private List<AdminAccountDto> adminList;//管理员
}

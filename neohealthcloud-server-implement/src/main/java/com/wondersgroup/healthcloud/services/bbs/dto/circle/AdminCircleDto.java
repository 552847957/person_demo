package com.wondersgroup.healthcloud.services.bbs.dto.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Created by limenghua on 2016/8/25.
 *
 * @author limenghua
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCircleDto {
    private Integer id;
    private String name;
    private String description;
    private Integer cateId;
    private String cateName;// 所属分类名称
    private String icon;
    private Integer rank;//排序
    private String delFlag;//默认为1 不启用 0：启用
    private Integer isRecommend;//是否推荐
    private Integer isDefaultAttent;//是否默认关注
}

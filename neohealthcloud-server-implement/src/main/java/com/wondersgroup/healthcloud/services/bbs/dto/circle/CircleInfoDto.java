package com.wondersgroup.healthcloud.services.bbs.dto.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * Created by limenghua on 2016/8/16.
 *
 * @author limenghua
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CircleInfoDto {
    private Integer id;
    private String name;
    private String icon;
    private String description;
    private Integer categoryId;
    private String categoryName;
    private Integer fakeAttentionCount;// 虚拟关注人数
    private Integer topicCount; // 话题总数
    private Integer attentionCount;// 真实关注人数
}

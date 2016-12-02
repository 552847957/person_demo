package com.wondersgroup.healthcloud.services.bbs.dto.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 圈子
 * Created by limenghua on 2016/8/12.
 *
 * @author limenghua
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CircleBannerDto {
    private String id;
    private String pic;
    private Integer topicId;
    private Integer picOrder;//图片展示顺序
    private String jumpUrl;// 跳转链接
}

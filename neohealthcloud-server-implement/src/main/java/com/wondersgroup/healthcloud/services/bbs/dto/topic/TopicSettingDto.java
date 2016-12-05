package com.wondersgroup.healthcloud.services.bbs.dto.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ys on 2016/08/11.
 * @author ys
 * 发布话题需要的字段
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicSettingDto {

    private Integer id = 0;
    private String uid;
    private Integer circleId;
    private Integer isBest=0;//是否精华推荐
    private Integer isTop=0;
    private Integer topRank=0;
    private Integer isPublish=0;
    private List<Integer> tags = new ArrayList<>();
}

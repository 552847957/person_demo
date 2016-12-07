package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * Created by ys on 2016/08/11.
 * @author ys
 * 回复需要的字段
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentPublishDto {

    private Integer id;
    private String uid;
    private Integer floor;
    private Integer topicId;
    private String content;
    private Integer isOwner=0;//是否是楼主回复
    private Integer referCommentId=0;

}

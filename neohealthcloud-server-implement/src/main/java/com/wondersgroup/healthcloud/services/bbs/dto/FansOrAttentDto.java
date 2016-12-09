package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 关注/粉丝列表
 * Created by ys on 2016/12/09.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FansOrAttentDto {
    private String fansUid;
    private String uid;
    private String nickname;
    private String avatar;
    private String gender;
    private int isAttention;
}

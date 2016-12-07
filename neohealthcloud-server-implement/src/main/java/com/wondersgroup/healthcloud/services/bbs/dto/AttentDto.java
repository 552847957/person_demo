package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 关注
 * Created by limenghua on 2016/8/17.
 *
 * @author limenghua
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttentDto {
    private String fansUid;
    private String attentUid;
    private String attentNickname;
    private String attentAvatar;
    private String attentGender;
    private String babyUid;
    private String babyName;
    private String babyBirthday;
    private String babyGender;
    private String babyAge;
    private int isAttention;
}

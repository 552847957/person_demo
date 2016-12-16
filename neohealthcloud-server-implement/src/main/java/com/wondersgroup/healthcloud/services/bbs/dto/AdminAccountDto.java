package com.wondersgroup.healthcloud.services.bbs.dto;

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
public class AdminAccountDto {
    private String id;
    private String nickname;
    private String avatar;
}

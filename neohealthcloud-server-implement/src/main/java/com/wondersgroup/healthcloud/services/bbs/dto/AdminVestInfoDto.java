package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import lombok.Data;

import java.util.Date;


/**
 * Created by ys on 2016/12/09.
 * 添加马甲用户
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminVestInfoDto {
    private Integer id;
    private String uid;
    private String nickName;
    private String avatar;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    private String gender;
}

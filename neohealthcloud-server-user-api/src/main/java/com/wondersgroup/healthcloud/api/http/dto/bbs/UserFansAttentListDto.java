package com.wondersgroup.healthcloud.api.http.dto.bbs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.services.bbs.dto.UserBbsInfo;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * </p>
 * 用户基本信息
 * Created by ys on 16/12/01.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFansAttentListDto {

    private String uid; //主键

    private String avatar;//头像

    private String nickname; //昵称

    private Boolean isBBsAdmin = false;//是否为bbs的管理员

    private Integer banStatus = UserConstant.BanStatus.OK;//圈子禁言状态(0:正常,-1:永久禁言,1:禁言1个小时,12:禁言12小时，24:禁言24小时(1天))

    private String gender;
    private Date birthday;

    private String delFlag="0";

    private int myAttentStatus=0;//0:未关注, 1:已关注, 2:已相互关注

    public UserFansAttentListDto(UserBbsInfo userBbsInfo) {
        try {
            BeanUtils.copyProperties(this, userBbsInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

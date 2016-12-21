package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import lombok.Data;


/**
 * Created by ys on 2016/8/15.
 * 圈子首页 (非个人圈子的首页)
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserHomeDto {
    private String uid;
    private Integer isAdmin = 0;
    private Boolean isIdentify = false;
    private String nickName;
    private String avatar;
    private Integer attentCount = 0;//关注数量
    private Integer fansCount = 0;//粉丝数量
    private Integer banStatus = 0;//圈子禁言状态(0:正常,-1:永久禁言,1:禁言1个小时,12:禁言12小时，24:禁言24小时(1天))
    private Integer attentStatus = 0;// 是否相互关注 0-没有相互关注 1-相互关注

    public void mergeOwnerUserInfo(RegisterInfo registerInfo){
        this.uid = registerInfo.getRegisterid();
        this.avatar = registerInfo.getHeadphoto();
        this.nickName = registerInfo.getNickname();
        this.isAdmin = registerInfo.getIsBBsAdmin();
        this.banStatus = registerInfo.getBanStatus();
        this.isIdentify = !registerInfo.getIdentifytype().equals("0");
        if (registerInfo.getBanStatus() == UserConstant.BanStatus.FOREVER){
            this.avatar = null;
        }
    }
}

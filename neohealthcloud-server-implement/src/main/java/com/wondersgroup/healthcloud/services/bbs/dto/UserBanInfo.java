package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserBanLog;
import lombok.Data;

import java.util.Date;

/**
 * </p>
 * 用户禁言信息
 * Created by ys on 16/12/01.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBanInfo {

    private Integer id; //ban log id

    private String uid; //用户uid

    private String adminUid;

    private String avatar;//管理员头像

    private String nickname; //管理员昵称

    private Integer banStatus = UserConstant.BanStatus.OK;//圈子禁言状态(0:正常,-1:永久禁言,1:禁言1个小时,12:禁言12小时，24:禁言24小时(1天))

    private String statusText;

    private String reason;

    private String msgTime;

    public UserBanInfo(){}

    public UserBanInfo(UserBanLog banLog){
        this.id = banLog.getId();
        this.uid = banLog.getUId();
        this.adminUid = banLog.getAdminUid();
        this.banStatus = banLog.getBanStatus();
        this.reason = banLog.getReason();
        this.msgTime = DateUtils.formatDate2Custom(banLog.getCreateTime());

        //-1永久 1一小时 12十二小时  24一天 0正常
        if(banStatus == -1){
            this.statusText = "永久禁言";
        }else if(banStatus == 1){
            this.statusText = "禁言一小时";
        }else if(banStatus == 12){
            this.statusText = "禁言十二小时";
        }else if(banStatus == 24){
            this.statusText = "禁言一天";
        }else if(banStatus == 0){
            this.statusText = "解除禁言";
            this.reason = "无";
        }
    }
}

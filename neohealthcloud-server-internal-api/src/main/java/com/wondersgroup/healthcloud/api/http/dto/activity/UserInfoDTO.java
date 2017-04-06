package com.wondersgroup.healthcloud.api.http.dto.activity;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;
import lombok.Data;

import java.util.List;

/**
 * Created by longshasha on 17/4/6.
 */
@Data
public class UserInfoDTO {

    private String registerid;

    private String nickname;

    private String sign_time;

    public UserInfoDTO (HealthActivityDetail detail){
        if(detail == null)
            return;
        this.registerid = detail.getRegisterid();
        this.nickname = detail.getNickname();
        this.sign_time = detail.getSigntime();

    }


    public static List<UserInfoDTO> infoDTO(List<HealthActivityDetail> detailList){
        if (detailList == null) {
            return Lists.newArrayList();
        }
        List<UserInfoDTO> infoDTO = Lists.newArrayList();
        for (HealthActivityDetail info : detailList) {
            infoDTO.add(new UserInfoDTO(info));
        }
        return infoDTO;

    }

}

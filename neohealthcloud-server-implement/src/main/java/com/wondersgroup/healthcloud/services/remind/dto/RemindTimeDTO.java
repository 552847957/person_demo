package com.wondersgroup.healthcloud.services.remind.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;
import lombok.Data;

import java.sql.Time;

/**
 * Created by Admin on 2017/4/11.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindTimeDTO {
    private String id;// id
    private String remindId;// 提醒ID
    private Time remindTime;// 提醒时间
    private String delFlag;// 删除标志

    public RemindTimeDTO() {
    }

    public RemindTimeDTO(RemindTime rt) {
        this.id = rt.getId();
        this.remindId = rt.getRemindId();
        this.remindTime = rt.getRemindTime();
        this.delFlag = rt.getDelFlag();
    }
}

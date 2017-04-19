package com.wondersgroup.healthcloud.services.remind.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.remind.Remind;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/4/11.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindDTO {
    private String id;// id
    private String userId;// 用户ID
    private String type;// 类型
    private String remark;// 备注
    private String delFlag;// 删除标志
    private List<RemindItemDTO> remindItems;// 提醒明细列表
    private List<RemindTimeDTO> remindTimes;// 提醒时间列表

    public RemindDTO() {
    }

    public RemindDTO(Remind r, List<RemindItem> remindItems, List<RemindTime> remindTimes) {
        this.id = r.getId();
        this.userId = r.getUserId();
        this.type = r.getType();
        this.remark = r.getRemark();
        this.delFlag = r.getDelFlag();

        if (remindItems != null && remindItems.size() > 0) {
            List<RemindItemDTO> remindItemList = new ArrayList();
            for (RemindItem ri : remindItems) {
                remindItemList.add(new RemindItemDTO(ri));
            }
            this.remindItems = remindItemList;
        }

        if (remindTimes != null && remindTimes.size() > 0) {
            List<RemindTimeDTO> remindTimeList = new ArrayList();
            for (RemindTime rt : remindTimes) {
                remindTimeList.add(new RemindTimeDTO(rt));
            }
            this.remindTimes = remindTimeList;
        }
    }
}

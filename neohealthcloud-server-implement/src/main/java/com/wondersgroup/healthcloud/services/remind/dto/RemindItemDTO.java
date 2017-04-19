package com.wondersgroup.healthcloud.services.remind.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import lombok.Data;

/**
 * Created by Admin on 2017/4/11.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindItemDTO {
    private String id;// id
    private String remindId;// 提醒ID
    private String medicineId;// 药品ID
    private String name;// 名称
    private String specification;// 规格
    private String dose;// 剂量
    private String unit;// 单位
    private String delFlag;// 删除标志

    public RemindItemDTO() {
    }

    public RemindItemDTO(RemindItem ri) {
        this.id = ri.getId();
        this.remindId = ri.getRemindId();
        this.medicineId = ri.getMedicineId();
        this.name = ri.getName();
        this.specification = ri.getSpecification();
        this.dose = ri.getDose();
        this.unit = ri.getUnit();
        this.delFlag = ri.getDelFlag();
    }
}

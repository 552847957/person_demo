package com.wondersgroup.healthcloud.jpa.entity.medicine;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

@Data
@Entity
@Table(name = "app_tb_commonly_used_medicine")
public class CommonlyUsedMedicine {
    @Id
    @Column(name = "id")
    private String id;// id
    @Column(name = "medicine_id")
    private String medicineId;// 药品ID
    @Column(name = "user_id")
    private String userId;// 用户ID
    @Column(name = "type")
    private String type;// 药品类型 M-药品,I-胰岛素
    @Column(name = "name")
    private String name;// 名称
    @Column(name = "specification")
    private String specification;// 规格
    @Column(name = "dose")
    private String dose;// 剂量
    @Column(name = "unit")
    private String unit;// 单位
    @Column(name = "del_flag")
    private String delFlag;// 删除标志 0:未删除,1:已删除
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;// 创建时间
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;// 更新时间

    public CommonlyUsedMedicine() {
    }

    public CommonlyUsedMedicine(RemindItem remindItem) {
        this.medicineId = remindItem.getMedicineId();
        this.name = remindItem.getName();
        this.specification = remindItem.getSpecification();
        this.dose = remindItem.getDose();
        this.unit = remindItem.getUnit();
    }
}
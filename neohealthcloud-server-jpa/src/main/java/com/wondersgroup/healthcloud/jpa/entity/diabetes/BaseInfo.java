package com.wondersgroup.healthcloud.jpa.entity.diabetes;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 职业字典表
 * Created by zhuchunliu on 2016/12/12.
 */
@Data
@Entity
@Table(name="t_dic_base_info")
public class BaseInfo {
    @Id
    private String id;
    private String   uid;
    private String   code; // 代码
    @Column(name= "item_order")
    private Integer   itemOrder; // 排序
    @Column(name= "explain_memo")
    private String   explainMemo;// 说明
    private String   pid; // 父id（职业用）
    @Column(name= "create_by")
    private String   createBy;
    @Column(name= "create_date")
    private Date createDate;
    @Column(name= "update_by")
    private String   updateBy;
    @Column(name= "update_date")
    private Date   updateDate;
    @Column(name= "del_flag")
    private String   delFlag;
    private String   remarks;
}

package com.wondersgroup.healthcloud.jpa.entity.remind;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaozhenxing on 2017/04/11.
 */

@Data
@Entity
@Table(name = "app_tb_remind")
public class Remind {
    @Id
    @Column(name = "id")
    private String id;// id
    @Column(name = "user_id")
    private String userId;// 用户ID
    @Column(name = "type")
    private String type;// 类型
    @Column(name = "remark")
    private String remark;// 备注
    @Column(name = "del_flag")
    private String delFlag;// 删除标志 0:未删除,1:已删除
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;// 创建时间
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;// 更新时间

    public Remind(){}

    public Remind(String id, String userId, String type, String remark, String delFlag) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.remark = remark;
    }
}
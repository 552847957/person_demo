package com.wondersgroup.healthcloud.jpa.entity.remind;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

/**
 * Created by zhaozhenxing on 2017/04/11.
 */

@Data
@Entity
@Table(name = "app_tb_remind_time")
public class RemindTime {
    @Id
    @Column(name = "id")
    private String id;// id
    @Column(name = "remind_id")
    private String remindId;// 提醒ID
    @Column(name = "remind_time")
    private Time remindTime;// 提醒时间
    @Column(name = "del_flag")
    private String delFlag;// 删除标志 0:未删除,1:已删除
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;// 创建时间
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;// 更新时间
}
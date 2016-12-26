package com.wondersgroup.healthcloud.jpa.entity.user.monitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

import lombok.Data;
import javax.persistence.*;

/**
 * Created by zhaozhenxing on 2016/12/13.
 */

@Data
@Entity
@Table(name = "app_tb_user_monitor")
public class UserMonitor {
    @Id
    @Column(name = "uid")
    private String uid;// UID
    @Column(name = "monitor_id")
    private String monitorId;// 监测方案ID app_tb_neoimage_text.adcode=12&main_area=3101&source=1 记录ID
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;// 创建时间
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;// 更新时间
    @Column(name = "del_flag")
    private String delFlag;// 删除标志 0：未删除 1：已删除
}
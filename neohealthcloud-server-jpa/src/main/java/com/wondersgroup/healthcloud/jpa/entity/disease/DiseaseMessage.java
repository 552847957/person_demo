package com.wondersgroup.healthcloud.jpa.entity.disease;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "app_tb_disease_message")
public class DiseaseMessage {
    @Id
    @Column(name = "id")
    public int id;
    @Column(name = "notifier_uid")
    public String notifierUid;
    @Column(name = "receiver_uid")
    public String receiverUid;

    /**
     0 干预提醒
     1 筛查提醒
     2 随访提醒
     3 血糖测量提醒
     4 报告提醒
     */
    @Column(name = "msg_type")
    public String msgType;
    @Column(name = "is_read")
    public String isRead;
    @Column(name = "jump_url")
    public String jumpUrl;
    @Column(name = "title")
    public String title;
    @Column(name = "content")
    public String content;
    @Column(name = "create_time")
    public Date   createTime;

    @Column(name="del_flag")
    public String delFlag;

}

package com.wondersgroup.healthcloud.jpa.entity.game;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/9/22.
 */
@Data
@Entity
@Table(name = "app_tb_wechat_register")
public class WechatRegister {
    @Id
    private String openid;
    private String registerid;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "del_flag")
    private String delFlag;
}

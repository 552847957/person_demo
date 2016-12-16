package com.wondersgroup.healthcloud.jpa.entity.bbs;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户粉丝
 * @author ys
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_fans")
public class UserFans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "uid")
    private String uid;

    @Column(name = "fans_uid")
    private String fansUid;

    @Column(name = "del_flag")
    private String delFlag = "0";

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}

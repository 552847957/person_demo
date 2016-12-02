package com.wondersgroup.healthcloud.jpa.entity.bbs;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by limenghua on 2016/8/17.
 *
 * @author limenghua
 */
@Entity
@Data
@Table(name = "tb_bbs_fans")
public class Fans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "uid")
    private String uId;
    @Column(name = "fans_uid")
    private String fansUid;
    @Column(name = "del_flag")
    private String delFlag = "0";
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "update_time")
    private String updateTime;
}

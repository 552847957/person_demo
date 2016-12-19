package com.wondersgroup.healthcloud.jpa.entity.bbs;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户关注圈子
 * Created by limenghua on 2016/8/15
 *
 * @author limenghua
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_user_circle")
public class UserCircle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uid")
    private String uId;

    @Column(name = "circle_id")
    private Integer circleId;

    @Column(name = "del_flag")
    private String delFlag = "0";

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}

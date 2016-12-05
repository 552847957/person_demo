package com.wondersgroup.healthcloud.jpa.entity.bbs;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 管理员的马甲用户
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_admin_vest")
public class AdminVestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String admin_uid;

    private String vest_uid;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "del_flag")
    private String delFlag="0";
}

package com.wondersgroup.healthcloud.jpa.entity.bbs;


import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming
@Table(name = "tb_bbs_admin_vest")
public class AdminVestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String admin_uid;//管理员的uid(regiest_info表的id)

    private String vest_uid;//马甲用户uid(regiest_info表的id)

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "del_flag")
    private String delFlag="0";
}

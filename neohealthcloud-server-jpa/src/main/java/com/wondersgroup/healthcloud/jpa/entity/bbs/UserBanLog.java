package com.wondersgroup.healthcloud.jpa.entity.bbs;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ys on 2016/8/21.
 *
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_user_ban_log")
public class UserBanLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uid")
    private String uId;

    @Column(name = "admin_uid")
    private String adminUid;

    @Column(name = "ban_status")
    private Integer banStatus;

    private String reason;

    @Column(name = "create_time")
    private Date createTime;
}

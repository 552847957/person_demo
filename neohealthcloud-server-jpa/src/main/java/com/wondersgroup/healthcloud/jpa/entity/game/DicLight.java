package com.wondersgroup.healthcloud.jpa.entity.game;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/10/20.
 */
@Data
@Entity
@Table(name = "app_tb_area_light")
public class DicLight {
    @Id
    private String id;
    private String registerid; // 游戏人
    @Column(name="area_code")
    private String areaCode;
    @Column(name="create_date")
    private Date createDate;
    @Column(name="update_date")
    private Date updateDate;
    @Column(name = "del_flag")
    private String delFlag;
}

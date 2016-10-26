package com.wondersgroup.healthcloud.jpa.entity.game;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/10/21.
 */
@Data
@Entity
@Table(name = "app_tb_prize_win")
public class PrizeWin {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String registerid;//中奖人主键
    private Integer prizeid;//奖品主键
    private String activityid;//活动详情
    @Column(name="create_date")
    private Date createDate;//中奖时间
    @Column(name = "del_flag")
    private String delFlag;
}

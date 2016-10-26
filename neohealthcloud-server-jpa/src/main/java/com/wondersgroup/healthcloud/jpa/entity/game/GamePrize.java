package com.wondersgroup.healthcloud.jpa.entity.game;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/10/21.
 */
@Data
@Entity
@Table(name = "app_tb_game_prize")
public class GamePrize {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;//奖品每次
    private Integer level;//奖品级别
    private Integer amount;//奖品数量
    @Column(name="game_id")
    private Integer gameId;
    @Column(name="update_date")
    private Date updateDate;
    @Column(name = "del_flag")
    private String delFlag;
}

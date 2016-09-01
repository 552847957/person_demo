package com.wondersgroup.healthcloud.jpa.entity.game;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
@Data
@Entity
@Table(name = "app_tb_game_score")
public class GameScore {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String registerid; // 游戏人
    private Integer score;//最高游戏分数
    private Integer count;//游戏玩的次数

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime; // 创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime; // 更新时间
}

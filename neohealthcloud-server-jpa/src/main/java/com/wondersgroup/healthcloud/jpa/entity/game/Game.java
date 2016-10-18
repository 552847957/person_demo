package com.wondersgroup.healthcloud.jpa.entity.game;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
@Data
@Entity
@Table(name = "app_tb_game")
public class Game {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String rule;
    @Column(name="start_time")
    private Date startTime;//开始时间
    @Column(name="end_time")
    private Date endTime;//结束时间
    @Column(name="weixin_click")
    private Integer weixinClick;//微信点击数
    @Column(name="app_click")
    private Integer appClick;//app点击数
    @Column(name="app_share")
    private Integer appShare;//app分享次数
    @Column(name="weixin_share")
    private Integer weixinShare;//微信分享次数
    @Column(name="max_score")
    private Integer maxScore;//微信分享次数
    private String type;//游戏类型  bacteria：细菌大作战，turntable：幸运大转盘


}

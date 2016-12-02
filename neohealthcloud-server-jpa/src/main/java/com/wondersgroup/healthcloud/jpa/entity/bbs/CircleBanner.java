package com.wondersgroup.healthcloud.jpa.entity.bbs;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by limenghua on 2016/8/11.
 */
@Entity
@Data
@Table(name = "tb_bbs_circle_banner")
public class CircleBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String pic;
    @Column(name = "topic_id")
    private Integer topicId;
    @Column(name = "pic_order")
    private Integer picOrder;//图片展示顺序
    @Column(name = "jump_url")
    private String jumpUrl;// 跳转链接
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "update_time")
    private String updateTime;
    @Column(name = "create_time")
    private String createTime;
}

package com.wondersgroup.healthcloud.jpa.entity.bbs;


import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 圈子
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_circle")
public class Circle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String icon;

    @Column(name = "cate_id")
    private Integer cateId;

    @Column(name = "is_recommend")
    private Integer isRecommend=0;//是否推荐 0:不推荐 1:推荐
    @Column(name = "is_default_attent")
    private Integer isDefaultAttent = 0;//是否默认关注 0：不关注 1：关注

    @Column(name = "topic_count")
    private Integer topicCount=0;//话题个数

    @Column(name = "attention_count")
    private Integer attentionCount=0;//关注人数

    @Column(name = "fake_attention_count")
    private Integer fakeAttentionCount=0;//虚假的关注人数

    private Integer rank=100;//排序

    @Column(name = "del_flag")
    private String delFlag="0";

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;


}

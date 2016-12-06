package com.wondersgroup.healthcloud.jpa.entity.bbs;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户话题点赞
 * Created by ys on 2016/10/21.
 */
@Entity
@Data
@Table(name = "tb_bbs_topic_favor")
public class TopicFavor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uid;

    @Column(name = "topic_id")
    private Integer topicId;//是否为精华贴

    @Column(name = "create_time")
    private Date createTime;

    public TopicFavor(){}

    public TopicFavor(String uid, Integer topicId){
        this.uid = uid;
        this.topicId = topicId;
        this.createTime = new Date();
    }
}

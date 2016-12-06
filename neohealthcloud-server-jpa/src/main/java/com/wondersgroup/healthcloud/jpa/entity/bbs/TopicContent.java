package com.wondersgroup.healthcloud.jpa.entity.bbs;


import lombok.Data;

import javax.persistence.*;

/**
 * 圈子话题 图文内容
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_topic_content")
public class TopicContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "topic_id")
    private Integer topicId;

    private String imgs;//图片,分割

    private String content;//内容

    public TopicContent(){}

    public TopicContent(Integer topicId, String content, String imgs){
        this.topicId = topicId;
        this.content = content;
        this.imgs = imgs;
    }
}

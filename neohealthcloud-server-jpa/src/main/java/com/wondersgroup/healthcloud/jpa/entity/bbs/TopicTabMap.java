package com.wondersgroup.healthcloud.jpa.entity.bbs;


import lombok.Data;

import javax.persistence.*;

/**
 * 圈子话题的标签 (多对多中间表)
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_topic_tab_map")
public class TopicTabMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tab_id")
    private Integer tabId;

    @Column(name = "topic_id")
    private Integer topicId;

    public TopicTabMap(){}

    public TopicTabMap(Integer topicId, Integer tabId){
        this.tabId = tabId;
        this.topicId = topicId;
    }
}

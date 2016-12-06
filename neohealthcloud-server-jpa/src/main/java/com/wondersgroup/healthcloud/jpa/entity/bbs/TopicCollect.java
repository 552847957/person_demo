package com.wondersgroup.healthcloud.jpa.entity.bbs;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 圈子话题
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_topic_collect")
public class TopicCollect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uid;

    @Column(name = "topic_id")
    private Integer topicId;//是否为精华贴

    @Column(name = "del_flag")
    private String delFlag="0";

    @Column(name = "create_time")
    private Date createTime;


}

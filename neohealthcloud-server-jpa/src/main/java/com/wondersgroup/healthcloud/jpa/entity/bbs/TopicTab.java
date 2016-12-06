package com.wondersgroup.healthcloud.jpa.entity.bbs;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 圈子话题的标签
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_topic_tab")
public class TopicTab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "circle_id")
    private Integer circleId;

    @Column(name = "tab_name")
    private String tabName;

    private Integer rank;

    @Column(name = "del_flag")
    private String delFlag="0";

    @Column(name = "create_time")
    private Date createTime;
}

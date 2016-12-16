package com.wondersgroup.healthcloud.jpa.entity.bbs;


import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 话题的投票
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_vote")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "topic_id")
    private Integer topicId;

    @Column(name = "start_time")
    private Date startTime;//投票开始时间(预留字段)

    @Column(name = "end_time")
    private Date endTime;//投票截止时间(预留字段)

    @Column(name = "is_single")
    private Integer isSingle=1;//是否未单选[1:单选,2:复选](预留字段)

    @Column(name = "vote_total_count")
    private Integer voteTotalCount=0;//总参与投票人数

    @Column(name = "create_time")
    private Date createTime;

    public Vote(){}

    public Vote(Integer topicId){
        this.topicId = topicId;
        Date nowDate = new Date();
        this.startTime = nowDate;
        this.endTime = DateUtils.parseString("2099-12-30 23:59:59");
        this.createTime = nowDate;
    }
}

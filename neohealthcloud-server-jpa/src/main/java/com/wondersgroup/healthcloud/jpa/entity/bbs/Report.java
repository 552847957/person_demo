package com.wondersgroup.healthcloud.jpa.entity.bbs;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ys on 2016/8/17.
 * 举报
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "target_id")
    private Integer targetId;//举报的对象(target_type=1表示话题id)

    @Column(name = "target_type")
    private Integer targetType = 1;//1:话题举报,2:回复举报

    @Column(name = "target_uid")
    private String targetUid = "";//举报内容的对象uid

    @Column(name = "report_count")
    private Integer reportCount = 1;//举报次数(每人一次)

    @Column(name = "first_report_uid")
    private String firstReportUid = "";//第一个举报人

    private Integer status=0;//0待处理，1：忽略 2：已处理

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;

}

package com.wondersgroup.healthcloud.jpa.entity.bbs;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ys on 2016/9/13.
 * 举报详情（每个人只能对一个话题/评论举报一次）
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_report_detail")
public class ReportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_id")
    private Integer reportId;//举报report的id

    private String uid;//举报人

    private Integer reason = 1;//举报原因

    @Column(name = "create_time")
    private Date createTime;

}

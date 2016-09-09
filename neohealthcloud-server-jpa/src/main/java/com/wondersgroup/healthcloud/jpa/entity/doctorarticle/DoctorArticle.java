package com.wondersgroup.healthcloud.jpa.entity.doctorarticle;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by shenbin on 16/8/30.
 */
@Data
@Entity
@Table(name = "app_tb_doctor_article")
public class DoctorArticle {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_ids")
    private String categoryIds;

    private String author;

    private String source;

    private String thumb;

    private String title;

    private String brief;

    private String content;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "fake_pv")
    private int fakePv;

    private int pv;

    @Column(name = "is_visable")
    private int isVisable;

    @Column(name = "online_time")
    private int onlineTime;

    @Column(name = "update_time")
    private int updateTime;

    @Column(name = "update_date")
    private Date updateDate;

    private String keyword;
}

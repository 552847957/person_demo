package com.wondersgroup.healthcloud.jpa.entity.doctorarticle;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by shenbin on 16/8/30.
 */
@Data
@Entity
@Table(name = "app_tb_doctor_article_category")
public class DoctorArticleCategory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private int rank;

    @Column(name = "c_name")
    private String caName;

    @Column(name = "is_visable")
    private int isVisable;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_time")
    private int updateTime;

    @Column(name = "update_date")
    private Date updateDate;
}

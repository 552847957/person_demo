package com.wondersgroup.healthcloud.jpa.entity.medicalcircle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/9/6.
 */
@Entity
@Data
@Table(name = "app_dic_tag")
public class MedicalCircleTag {

    @Id
    private String id;
    private String charid;
    private String tagname;
    private String tagmemo;
    private String tagsort;
    private String tagcolor;


    @Column(name = "del_flag")
    private String delFlag = "0";

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_date")
    private Date updateDate;

}

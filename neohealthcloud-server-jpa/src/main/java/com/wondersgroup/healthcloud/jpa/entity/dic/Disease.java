package com.wondersgroup.healthcloud.jpa.entity.dic;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/10.
 */
@Data
@Entity
@Table(name = "app_tb_disease")
public class Disease {

    @Id
    private String id;

    @Column(name = "disease_name")
    private String diseaseName;

    @Column(name = "attach_id")
    private String attachId;

    @Column(name = "disease_desc")
    private String diseaseDesc;

    @Column(name = "online_time")
    private Date onlineTime;

    @Column(name = "is_visable")
    private Integer isVisable;

    @Column(name = "is_chronic")
    private String isChronic;
}

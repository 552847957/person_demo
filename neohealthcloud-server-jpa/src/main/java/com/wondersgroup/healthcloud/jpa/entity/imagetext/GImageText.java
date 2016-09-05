package com.wondersgroup.healthcloud.jpa.entity.imagetext;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaozhenxing on 2016/8/28.
 */
@Data
@Entity
@Table(name = "app_tb_neo_g_image_text")
public class GImageText {
    @Id
    private String id;
    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;
    @Column(name = "allow_close")
    private String allowClose;
    private String remark;
    private String version;
    @Column(name = "main_area")
    private String mainArea;
    @Column(name = "spec_area")
    private String specArea;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    private Integer gadcode;
    @Column(name = "del_flag")
    private String delFlag;
    private String source;
    @Transient
    private List<ImageText> images;
}

package com.wondersgroup.healthcloud.jpa.entity.medicalcircle;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 医生圈信息
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_medicalcircle")
public class MedicalCircle implements  Serializable {
    private static final long serialVersionUID = 8057604029718771988L;
    private String  doctorid;  //医生id
    private Date    sendtime;  //发送时间
    private String  tagid;     //标签id，医生圈标签表中的id
    private Integer type;      //1:帖子 2:病例 3:动态
    private String  content;   //内容
    private String  title;     //标题
    private Long    views;     //阅读数
    private Long    praisenum; //点赞数
    private String  transmitid;
    private String  tagnames;

    @Column(name = "is_visible")
    private String  isVisible; //是否冻结
    
    @Id
    private String id;
    @Column(name = "del_flag")
    private String delFlag;
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

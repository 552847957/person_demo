package com.wondersgroup.healthcloud.jpa.entity.faq;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by longshasha on 16/8/16.
 * 问答集锦
 */
@Data
@Entity
@Table(name = "faq_question_tb")
public class Faq {

    @Id
    private String id;

    @Column(name = "q_id")
    private String qId;

    @Column(name = "q_pid")
    private String qPid;

    private Integer type;

    @Column(name = "asker_name")
    private String askerName;

    private Integer gender;

    private Integer age;

    @Column(name = "ask_content")
    private String askContent;

    @Column(name = "is_show")
    private Integer isShow;

    @Column(name = "is_top")
    private Integer isTop;

    @Column(name = "ask_date")
    private Date askDate;

    @Column(name = "doctor_id")
    private String doctorId;

    @Column(name = "answer_content")
    private String answerContent;

    @Column(name = "answer_date")
    private Date answerDate;

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

    @Transient
    private String doctor_answer_id;
}

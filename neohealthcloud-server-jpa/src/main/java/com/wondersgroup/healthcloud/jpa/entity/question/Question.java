package com.wondersgroup.healthcloud.jpa.entity.question;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by dukuanxin on 16/8/10.
 */
@Data
@Entity
@Table(name = "app_tb_neoquestion")
public class Question {
    @Id
    private String id;
    private int status=1;
    @Column(name="has_noread_comment")
    private int hasReply;
    @Column(name="asker_id")
    private String askerId;
    @Column(name="assign_answer_id")
    private String answerId="";
    private int sex;
    private int age;
    private int is_new_question=1;//医生未读消息
    private Integer comment_count;//回复的医生数量
    private String newest_answer_id;//最后一次回复的医生id
    private String content;
    @Column(name="content_imgs")
    private String contentImgs;
    @Column(name="is_valid")
    private int isValid;
    @Column(name="create_time")
    private Date createTime;
    @Column(name="update_time")
    private Date upDate;
}

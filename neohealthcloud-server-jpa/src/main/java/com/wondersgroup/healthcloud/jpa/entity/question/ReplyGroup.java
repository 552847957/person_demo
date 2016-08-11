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
@Table(name = "app_tb_neogroup")
public class ReplyGroup {

    @Id
    private String id;

    @Column(name="question_id")
    private String question_id;

    @Column(name="answer_id")
    private String answer_id;

    @Column(name = "has_new_user_comment")
    private int hasNewUserComment;//是否有用户的评论

    @Column(name="new_comment_time")
    private Date newCommentTime;

    @Column(name="is_valid")
    private Integer is_valid;

    @Column(name="create_time")
    private Date createTime;

}

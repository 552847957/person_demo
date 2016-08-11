package com.wondersgroup.healthcloud.jpa.entity.question;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
/**
 * Created by dukuanxin on 16/8/10.
 */
@Data
@Entity
@Table(name = "app_tb_neoreply")
public class Reply {
    @Id
    @GeneratedValue(generator = "uuidgenerator")
    @GenericGenerator(name = "uuidgenerator", strategy = "uuid")
    private String id;
    @Column(name="comment_group_id")
    private String groupId;
    @Column(name="is_user_reply")
    private int userReply;

    private String content;
    @Column(name="content_imgs")
    private String contentImgs;
    @Column(name="is_valid")
    private int isValid;
    @Column(name="create_time")
    private Date createTime;

}

package com.wondersgroup.healthcloud.jpa.entity.bbs;


import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.constant.CommentConstant;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 圈子话题评论
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "topic_id")
    private Integer topicId;

    private String uid;

    private Integer status= CommentConstant.Status.OK;//帖子状态:0:待审核,1:正常,2:已删除

    private Integer floor=0;//楼层

    @Column(name = "refer_comment_id")
    private Integer referCommentId=0;

    @Column(name = "refer_uid")
    private String referUId="";

    @Column(name = "is_owner")
    private Integer isOwner=0;//是否是楼主评论的

    private String content;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;

}

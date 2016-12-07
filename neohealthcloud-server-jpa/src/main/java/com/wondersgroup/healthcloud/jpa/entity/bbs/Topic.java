package com.wondersgroup.healthcloud.jpa.entity.bbs;


import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 圈子话题
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "circle_id")
    private Integer circleId;

    private String uid;

    private String title;

    private Integer status= TopicConstant.Status.OK;//帖子状态:-1:待发布,0:待审核,1:正常,2:禁止回复,3:已删除

    @Column(name = "is_best")
    private Integer isBest=0;//是否为精华贴

    @Column(name = "is_vote")
    private Integer isVote=0;//是否包含投票

    @Column(name = "is_top")
    private Integer isTop=0;//是否置顶

    @Column(name = "top_rank")
    private Integer topRank=0;//置顶的优先级值越大优先级越高

    private String imgs="";//列表显示的图片

    private String intro="";//简介,列表显示用

    @Column(name = "img_count")
    private Integer imgCount=0;//话题包含的图片个数

    @Column(name = "comment_count")
    private Integer commentCount=0;//话题的评论数

    private Integer pv=0;//浏览数

    private Integer score=0;//得分

    @Column(name = "favor_count")
    private Integer favorCount=0;//点赞数

    @Column(name = "last_comment_time")
    private Date lastCommentTime;//最近一次的话题评论时间

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;


}

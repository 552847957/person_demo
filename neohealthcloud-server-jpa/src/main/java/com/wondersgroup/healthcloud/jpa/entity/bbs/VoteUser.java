package com.wondersgroup.healthcloud.jpa.entity.bbs;


import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户的投票情况
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@JsonNaming
@Table(name = "tb_bbs_vote_user")
public class VoteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vote_id")
    private Integer voteId;

    @Column(name = "vote_item_id")
    private Integer voteItemId;

    private String uid;

    @Column(name = "create_time")
    private Date createTime;
}

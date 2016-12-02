package com.wondersgroup.healthcloud.jpa.entity.bbs;


import lombok.Data;

import javax.persistence.*;

/**
 * 投票的每个小项
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_vote_item")
public class VoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vote_id")
    private Integer voteId;

    private String content;//当前选项内容

    @Column(name = "vote_count")
    private Integer voteCount=0;//当前选项投票人数

    public VoteItem(){}

    public VoteItem(Integer voteId, String content){
        this.voteId = voteId;
        this.content = content;
    }
}

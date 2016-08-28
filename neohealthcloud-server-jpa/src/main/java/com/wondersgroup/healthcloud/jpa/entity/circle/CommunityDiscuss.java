package com.wondersgroup.healthcloud.jpa.entity.circle;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 健康圈文章评论
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_communitydiscuss")
public class CommunityDiscuss extends BaseEntity{
    private String registerid;
    private String articleid;
    private Date discusstime;
    private String content;

}

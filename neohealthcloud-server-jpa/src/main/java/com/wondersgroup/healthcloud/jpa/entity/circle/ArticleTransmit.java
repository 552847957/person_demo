package com.wondersgroup.healthcloud.jpa.entity.circle;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 健康圈转发文章列表
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_transmitarticle")
public class ArticleTransmit extends BaseEntity {
    private String title;
    private String subtitle;
    private String pic;
    private String url;
}

package com.wondersgroup.healthcloud.jpa.entity.circle;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 健康圈附件
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_articleattach")
public class ArticleAttach extends BaseEntity{
    private String articleid;
    private String attachid;
    private Integer attachtype;
    private Integer sort;

}

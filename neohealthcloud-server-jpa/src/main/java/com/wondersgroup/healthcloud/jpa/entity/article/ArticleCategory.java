package com.wondersgroup.healthcloud.jpa.entity.article;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by dukuanxin on 2016/8/17.
 */
@Data
@Entity
@Table(name="app_tb_article_category")
public class ArticleCategory implements Serializable{

    private static final long serialVersionUID = -4051859403776354544L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int disease_id;
    private int rank;
    private String name;
    private String update_by;
    private int is_visable;
    private int update_time;

}

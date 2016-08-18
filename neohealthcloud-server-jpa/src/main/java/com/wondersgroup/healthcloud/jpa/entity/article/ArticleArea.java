package com.wondersgroup.healthcloud.jpa.entity.article;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by dukuanxin on 2016/8/17.
 */
@Data
@Entity
@Table(name="app_tb_article_area")
public class ArticleArea {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    private int article_id;
    private String area_id;
    private String is_visable;
    private Date create_time;
}

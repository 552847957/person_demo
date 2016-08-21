package com.wondersgroup.healthcloud.jpa.entity.article;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by dukuanxin on 2016/8/17.
 */
@Data
@Entity
@Table(name="app_tb_news_article_category")
public class NewsArticleCategory implements Serializable {
    private static final long serialVersionUID = -4051859403776354544L;
    @Id
    private int id;// 分类ID
    private String c_name;// 分类名称
    private int rank;// 排序
    private int is_visable;// 是否有效(1:有效,0:无效)
    private String update_by;// 更新人
    private Date update_time;// 更新时间

}

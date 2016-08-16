package com.wondersgroup.healthcloud.jpa.entity.article;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/30.
 */
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
    private int update_time;// 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getIs_visable() {
        return is_visable;
    }

    public void setIs_visable(int is_visable) {
        this.is_visable = is_visable;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public int getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }
}

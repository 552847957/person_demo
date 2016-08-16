package com.wondersgroup.healthcloud.jpa.entity.article;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 文章分类管理
 * @author ys
 *
 */
@Entity
@Table(name="app_tb_article_category")
public class ArticleCategory implements Serializable{

    private static final long serialVersionUID = -4051859403776354544L;
    @Id
    private int id;
    private int disease_id;
    private int rank;
    private String name;
    private String update_by;
    private int is_visable;
    private int update_time;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getDisease_id() {
        return disease_id;
    }
    public void setDisease_id(int disease_id) {
        this.disease_id = disease_id;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUpdate_by() {
        return update_by;
    }
    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }
    public int getIs_visable() {
        return is_visable;
    }
    public void setIs_visable(int is_visable) {
        this.is_visable = is_visable;
    }
    public int getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }


}

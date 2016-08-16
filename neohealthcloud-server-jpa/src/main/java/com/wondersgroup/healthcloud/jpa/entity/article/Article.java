package com.wondersgroup.healthcloud.jpa.entity.article;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 文章管理
 * @author ys
 *
 */
@Entity
@Table(name="app_tb_article")
public class Article implements Serializable{

    private static final long serialVersionUID = -4051859403776354544L;
    @Id
    private int id;
    private int disease_id;
    private String author;
    private String thumb;//缩略图
    private String title;//文章标题
    private String brief;//文章描述
    private String content;
    private String category_ids;
    private String update_by;
    private String source;
    private int fake_pv;
    private int pv;
    private int is_visable;
    private int online_time;
    private int update_time;
    private String keyword;
    private int type;

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
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCategory_ids() {
        return category_ids == null ? "" : category_ids;
    }
    public void setCategory_ids(String category_ids) {
        this.category_ids = category_ids;
    }
    public String getUpdate_by() {
        return update_by;
    }
    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }
    public int getFake_pv() {
        return fake_pv;
    }
    public void setFake_pv(int fake_pv) {
        this.fake_pv = fake_pv;
    }
    public int getPv() {
        return pv;
    }
    public void setPv(int pv) {
        this.pv = pv;
    }
    public int getIs_visable() {
        return is_visable;
    }
    public void setIs_visable(int is_visable) {
        this.is_visable = is_visable;
    }
    public int getOnline_time() {
        return online_time;
    }
    public void setOnline_time(int online_time) {
        this.online_time = online_time;
    }
    public int getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getThumb() {
        return thumb;
    }
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public String getBrief() {
        return brief;
    }
    public void setBrief(String brief) {
        this.brief = brief;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

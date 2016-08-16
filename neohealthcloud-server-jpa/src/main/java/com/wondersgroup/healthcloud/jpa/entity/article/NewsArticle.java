package com.wondersgroup.healthcloud.jpa.entity.article;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/30.
 */
@Entity
@Table(name="app_tb_news_article")
public class NewsArticle implements Serializable {
    @Id
    private int id;// 文章ID
    private String category_ids;// 文章分类的ids(,分割)
    private String author;// 作者
    private String source;// 来源
    private String thumb;//缩略图
    private String title;//文章标题
    private String brief;//文章描述
    private String content;// 文章内容
    private String update_by;// 更新人
    private int fake_pv;// 虚拟阅读量
    private int pv;// 实际阅读量
    private int is_visable;// 是否有效(1:有效,0:无效)
    private int online_time;// 上线时间
    private int update_time;// 更新时间
    private String keyword;// 关键字

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory_ids() {
        return category_ids;
    }

    public void setCategory_ids(String category_ids) {
        this.category_ids = category_ids;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

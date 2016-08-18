package com.wondersgroup.healthcloud.services.article.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;

/**
 * Created by yanshuai on 15/6/26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsArticleListAPIEntity {
    private String id;
    private String thumb;
    private String title;
    private String desc;
    private String pv;
    private String url;

    public NewsArticleListAPIEntity(NewsArticle article){
        this.id = String.valueOf(article.getId());
        this.title = article.getTitle();
        this.desc= article.getBrief();
        this.pv = String.valueOf(article.getPv() + article.getFake_pv());
        this.thumb = article.getThumb();
        this.url = AppUrlH5Utils.buildNewsArticleView(article.getId());

    }
    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
}

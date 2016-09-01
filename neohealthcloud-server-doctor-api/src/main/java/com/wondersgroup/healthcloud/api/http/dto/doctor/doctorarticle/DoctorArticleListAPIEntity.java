package com.wondersgroup.healthcloud.api.http.dto.doctor.doctorarticle;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by yanshuai on 15/6/26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorArticleListAPIEntity {
    private String id;
    private String thumb;
    private String title;
    private String desc;
    private String pv;
    private String url;

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

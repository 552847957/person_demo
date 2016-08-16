package com.wondersgroup.healthcloud.jpa.entity.article;

public class ArticleSearchCriteria extends BaseSearchCriteria {

    private Integer cat_id;
    private Integer is_visable;
    private String title;
    private Integer disease_id = 0;
    public Integer getCat_id() {
        return cat_id;
    }
    public void setCat_id(Integer cat_id) {
        this.cat_id = cat_id;
    }

    public Integer getIs_visable() {
        return is_visable;
    }
    public void setIs_visable(Integer is_visable) {
        this.is_visable = is_visable;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getDisease_id() {
        return disease_id;
    }
    public void setDisease_id(Integer disease_id) {
        this.disease_id = disease_id;
    }
}

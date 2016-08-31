package com.wondersgroup.healthcloud.api.http.dto.doctor.doctorarticle;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;

/**
 * Created by shenbin on 16/8/31.
 */
public class DoctorArticleDto {

    private int articleId;

    private String desc;

    private int pv;

    private String thumb;

    private String title;

    private String url;

    public DoctorArticleDto toNewDoctorArticleDto(DoctorArticle doctorArticle){
        DoctorArticleDto doctorArticleDto = new DoctorArticleDto();
        doctorArticleDto.setArticleId(doctorArticle.getId());
        doctorArticleDto.setDesc(doctorArticle.getContent());
        doctorArticleDto.setPv(doctorArticle.getPv());
        doctorArticleDto.setThumb(doctorArticle.getThumb());
        doctorArticleDto.setTitle(doctorArticle.getTitle());

        return doctorArticleDto;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

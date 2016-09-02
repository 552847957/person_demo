package com.wondersgroup.healthcloud.api.http.dto.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteAPIEntity {
    private String               title;      //标题
    private Boolean              has_images;
    private String               content;    //内容
    private List<ImageAPIEntity> images;     //图片集合

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getHas_images() {
        return has_images;
    }

    public void setHas_images(Boolean has_images) {
        this.has_images = has_images;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ImageAPIEntity> getImages() {
        return images;
    }

    public void setImages(List<ImageAPIEntity> images) {
        this.images = images;
    }
}

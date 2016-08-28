package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Yoda on 2015/9/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteAPIEntity {
    private String title;
    private Boolean has_images;
    private String content;
    private List<ImageAPIEntity> images;

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

package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Yoda on 2015/9/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DynamicAPIEntity {
    private String content;
    private ShareAPIEntity share;
    private List<ImageAPIEntity> images;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ShareAPIEntity getShare() {
        return share;
    }

    public void setShare(ShareAPIEntity share) {
        this.share = share;
    }

    public List<ImageAPIEntity> getImages() {
        return images;
    }

    public void setImages(List<ImageAPIEntity> images) {
        this.images = images;
    }
}

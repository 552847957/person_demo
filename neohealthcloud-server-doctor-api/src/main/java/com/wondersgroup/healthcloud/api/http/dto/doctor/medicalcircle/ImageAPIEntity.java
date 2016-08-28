package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Yoda on 2015/7/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageAPIEntity {
    private Float ratio; //长宽比
    private String url; //标准图
    private String type; //图片类型
    private String thumb; //缩略图
    private Integer height;
    private Integer width;

    public Float getRatio() {
        return ratio;
    }

    public void setRatio(Float ratio) {
        this.ratio = ratio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}

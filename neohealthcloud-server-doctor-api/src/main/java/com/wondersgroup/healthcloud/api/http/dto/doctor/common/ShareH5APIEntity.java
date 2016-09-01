package com.wondersgroup.healthcloud.api.http.dto.doctor.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.common.utils.PropertiesUtils;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareH5APIEntity {

    private String thumb = PropertiesUtils.get("SHARE.THUMB.URL");
    private String title;
    private String desc;
    private String url;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

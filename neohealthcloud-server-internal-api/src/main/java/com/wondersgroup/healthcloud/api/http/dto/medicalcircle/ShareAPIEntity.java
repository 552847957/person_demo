package com.wondersgroup.healthcloud.api.http.dto.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.common.utils.PropertiesUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareAPIEntity {
    private String title;                                          //标题
    private String desc;                                           //描述
    private String thumb = PropertiesUtils.get("SHARE.THUMB.URL");
    private String url;

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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

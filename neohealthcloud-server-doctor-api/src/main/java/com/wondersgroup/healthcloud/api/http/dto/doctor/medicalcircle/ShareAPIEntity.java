package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.common.utils.PropertiesUtils;

/**
 * Created by Yoda on 2015/7/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareAPIEntity {
    private String title;
    private String desc;
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

package com.wondersgroup.healthcloud.api.http.dto.measure;

import com.wondersgroup.healthcloud.api.http.controllers.measure.HealthType;

/**
 * Created by Jeffrey on 16/8/19.
 */
public class MeasureTypeDTO {

    private String title;

    private String desc;

    private String iconUrl;

    private HealthType type;

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getType() {
        return type.ordinal();
    }

    public void setType(HealthType type) {
        this.type = type;
    }
}

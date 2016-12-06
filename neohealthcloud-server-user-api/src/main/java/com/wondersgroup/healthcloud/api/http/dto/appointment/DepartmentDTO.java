package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by longshasha on 16/5/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDTO {
    public static final String url = "http://img.wdjky.com/aae0e58c1451899781525.png?imageView2";

    private String id;
    private String name;
    private String level;
    private String avatar = url;

    @JsonProperty("can_reserve")
    private Boolean  canReserve; //二级科室是否可预约

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getCanReserve() {
        return canReserve;
    }

    public void setCanReserve(Boolean canReserve) {
        this.canReserve = canReserve;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

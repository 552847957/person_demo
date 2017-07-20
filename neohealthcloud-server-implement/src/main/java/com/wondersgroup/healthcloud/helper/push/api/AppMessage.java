package com.wondersgroup.healthcloud.helper.push.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.helper.push.exception.AppMessageNotCompleteException;
import com.wondersgroup.healthcloud.helper.push.getui.PushMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 *     app message 封装类
 * Created by zhangzhixiu on 8/17/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppMessage {

    public String id;
    public String title;
    public String content;
    public Boolean persistence;//是否保存到消息表中
    public String area;//指定的地区
    @JsonProperty("area_special")
    public Boolean areaSpecial;//是否为地区独有内容相关的信息
    public AppMessageUrlUtil.Type type;
    @JsonProperty("is_doctor")
    public Boolean isDoctor;
    @JsonProperty("url_fragment")
    public String urlFragment;
    public Map<String, String> params;

    public AppMessage() {
    }

    private AppMessage(String id, String title, String content, Boolean persistence, String area, Boolean areaSpecial, AppMessageUrlUtil.Type type, Boolean isDoctor, String urlFragment, Map<String, String> params) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.persistence = persistence;
        this.area = area;
        this.areaSpecial = areaSpecial;
        this.type = type;
        this.isDoctor = isDoctor;
        this.urlFragment = urlFragment;
        this.params = params;
    }

    public PushMessage toPushMessage() {
        return new PushMessage(id, title, content, buildUrl(), params);
    }

    private String buildUrl() {
        if (urlFragment == null) {
            return null;
        }
        if (type == AppMessageUrlUtil.Type.ACTIVITY) {
            type = AppMessageUrlUtil.Type.SYSTEM;
            return urlFragment;
        }
        if (type != AppMessageUrlUtil.Type.HTTP) {
            return buildAppUrl(area, isDoctor, urlFragment);
        } else {
            return urlFragment;
        }
    }

    public static String buildAppUrl(String area, Boolean isDoctor, String urlFragment) {
        if (urlFragment == null) {
            return null;
        }
        return "com.wondersgroup.healthcloud." + area + "://" + (isDoctor ? "doctor" : "user") + urlFragment;
    }

    public static class Builder {
        private String id;
        private String title;
        private String content;
        private boolean persistence;
        private String area;
        private boolean areaSpecial;
        private AppMessageUrlUtil.Type type;
        private boolean isDoctor;
        private String urlFragment;
        private Map<String, String> params;

        public static Builder init() {
            return new Builder();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder persistence() {
            this.persistence = true;
            return this;
        }

        public Builder areaSpecial() {
            this.areaSpecial = true;
            return this;
        }

        public Builder type(AppMessageUrlUtil.Type type) {
            this.type = type;
            return this;
        }

        public Builder isDoctor() {
            this.isDoctor = true;
            return this;
        }

        public Builder urlFragment(String urlFragment) {
            this.urlFragment = urlFragment;
            return this;
        }

        public Builder param(String key, String value) {
            if (this.params == null) {
                this.params = new HashMap<>(4);
            }
            this.params.put(key, value);
            return this;
        }

        public AppMessage build() {
            if (id == null && persistence) {
                this.id = IdGen.uuid();
            }
            return new AppMessage(id, title, content, persistence, area, areaSpecial, type, isDoctor, urlFragment, params);
        }

        private void check() {
            if (title == null || content == null || type == null) {
                throw new AppMessageNotCompleteException("");
            }
        }

    }
}
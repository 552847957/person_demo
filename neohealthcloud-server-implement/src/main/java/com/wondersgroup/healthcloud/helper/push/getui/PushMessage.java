package com.wondersgroup.healthcloud.helper.push.getui;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
 * Created by zhangzhixiu on 8/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushMessage {

    public final String id;
    public final String title;
    public final String content;
    public final String url;
    public final Map<String, String> params;

    private PushMessage(String title, String content, String url, Map<String, String> params) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.url = url;
        this.params = params;
    }

    public static class Builder {
        private String title;
        private String content;
        private String url;
        private Map<String, String> params;

        public static Builder init() {
            return new Builder();
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder param(String key, String value) {
            if (this.params == null) {
                this.params = new HashMap<>(4);
            }
            this.params.put(key, value);
            return this;
        }

        public PushMessage build() {
            return new PushMessage(title, content, url, params);
        }
    }
}

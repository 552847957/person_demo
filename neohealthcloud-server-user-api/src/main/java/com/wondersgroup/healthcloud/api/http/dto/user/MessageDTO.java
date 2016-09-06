package com.wondersgroup.healthcloud.api.http.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import com.wondersgroup.healthcloud.utils.DateFormatter;

import java.util.Date;

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
 * Created by zhangzhixiu on 8/19/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDTO {

    private static final Long millisecondOfDay = 24 * 60 * 60 * 1000L;

    public String id;
    public String title;
    public String content;
    public String url;
    public String icon;
    public String type;
    public String time;
    @JsonProperty("is_read")
    public Boolean isRead;
    @JsonIgnore
    public UserPrivateMessage nativeMessage;

    public MessageDTO() {

    }

    public MessageDTO(UserPrivateMessage message, String area) {
        this.id = message.getId();
        this.title = message.getTitle();
        this.content = message.getContent();
        this.type = message.getType();
        this.url = "-1".equals(type) ? message.getUrl() : AppMessage.buildAppUrl(area, false, message.getUrl());
        this.time = parseDate(message.getCreateTime());
        this.isRead = message.getIsRead();
        this.nativeMessage = message;
    }

    private static String parseDate(Date date) {
        long day = date.getTime() / millisecondOfDay;
        long now = System.currentTimeMillis() / millisecondOfDay;
        if (now - day == 0L) {
            return DateFormatter.format(date, "HH:mm");
        } else if (now - day == 1L) {
            return DateFormatter.format(date, "昨天 HH:mm");
        } else if (now - day == 2L) {
            return DateFormatter.format(date, "前天 HH:mm");
        } else {
            return DateFormatter.format(date, "yyyy-MM-dd");
        }
    }
}

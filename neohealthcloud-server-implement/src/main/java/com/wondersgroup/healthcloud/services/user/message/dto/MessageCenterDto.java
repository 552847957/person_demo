package com.wondersgroup.healthcloud.services.user.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.utils.DateFormatter;

import java.util.Date;

/**
 * 消息中心-根列表实体
 * Created by jialing.yao on 2016-12-13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageCenterDto {
    private static final Long millisecondOfDay = 24 * 60 * 60 * 1000L;
    private String title;
    private String content;
    private String icon="";
    private String type;//消息类型
    private String time="";
    private Boolean isRead=false;
    private int sort;

    public static String parseDate(Date date) {
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

    @JsonProperty("is_read")
    public Boolean getIsRead() {
        return isRead;
    }
    @JsonProperty("is_read")
    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}

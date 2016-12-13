package com.wondersgroup.healthcloud.services.user.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import lombok.Data;

import java.util.Date;

/**
 * 消息中心-根列表实体
 * Created by jialing.yao on 2016-12-13.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageCenterDto {
    private static final Long millisecondOfDay = 24 * 60 * 60 * 1000L;
    private String title;
    private String content;
    private String icon;
    private String type;//消息类型
    private String time;
    @JsonProperty("is_read")
    private boolean isRead;
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
}

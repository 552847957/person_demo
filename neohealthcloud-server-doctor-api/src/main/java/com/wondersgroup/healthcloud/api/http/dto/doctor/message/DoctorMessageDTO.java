package com.wondersgroup.healthcloud.api.http.dto.doctor.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.enums.DoctorMsgTypeEnum;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by longshasha on 17/5/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DoctorMessageDTO {

    private String id;
    private String title;

    @JsonProperty("msg_type")
    private String msgType;

    @JsonProperty("type_name")
    private String typeName;

    private String content;

    @JsonProperty("has_unread")
    private Boolean hasUnread;

    @JsonProperty("time_str")
    private String timeStr;

    public DoctorMessageDTO (DoctorMessage doctorMessage){
        this.id = doctorMessage.getId();
        this.title = doctorMessage.getTitle();
        this.msgType = doctorMessage.getMsgType();
        this.typeName = DoctorMsgTypeEnum.fromTypeCode(doctorMessage.getMsgType()).getTypeName();
        this.content = doctorMessage.getContent();

        // 若时间为今天则显示具体时间到分钟［10:30］，
        // 若时间为昨天则显示昨天＋具体时间到分钟［昨天 10:30］，
        // 若时间为前天则显示前天＋具体时间到分钟［前天 10:30］，
        // 若时间早于前天则只显示日期［2016-05-14］

        SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
        Date today = DateFormatter.parseDate(fmt.format(new Date()).toString());
        //消息发送时间
        Date updateDate = DateFormatter.parseDateTime(doctorMessage.getUpdateDate());

        Date yesterday = DateUtils.addDay(today,-1);
        Date beforeYesterday = DateUtils.addDay(today,-2);
        if(DateUtils.compareDate(updateDate,today)>=0){
            this.timeStr = DateFormatter.hourDateFormat(updateDate);
        }else if(DateUtils.compareDate(updateDate,yesterday)>=0){
            this.timeStr = "昨天 "+DateFormatter.hourDateFormat(updateDate);
        }else if(DateUtils.compareDate(updateDate,beforeYesterday)>=0){
            this.timeStr = "前天 "+DateFormatter.hourDateFormat(updateDate);
        }else{
            this.timeStr = DateFormatter.dateFormat(updateDate);
        }


    }

}

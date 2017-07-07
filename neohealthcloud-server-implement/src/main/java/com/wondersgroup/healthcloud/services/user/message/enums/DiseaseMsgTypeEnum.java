package com.wondersgroup.healthcloud.services.user.message.enums;

import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import org.apache.commons.lang3.StringUtils;

/**
 * 慢病消息类型枚举
 * Created by jialing.yao on 2016-12-12.
 */
public enum DiseaseMsgTypeEnum {
    /*
        0 干预提醒
        1 筛查提醒
        2 随访提醒
        3 血糖测量提醒(归到系统消息下面)
        4 报告提醒
     */
    msgType0("0"),
    msgType1("1"),
    msgType2("2"),
    msgType3("3"),
    msgType4("4");


    private final String typeCode;
    DiseaseMsgTypeEnum(String code) {
        typeCode=code;
    }
    public String getTypeCode() {
        return typeCode;
    }

    public static DiseaseMsgTypeEnum fromTypeCode(String v) {
        if(StringUtils.isNotBlank(v)){
            for (DiseaseMsgTypeEnum c: DiseaseMsgTypeEnum.values()) {
                if (c.getTypeCode().equals(v)) {
                    return c;
                }
            }
            throw new EnumMatchException("慢病消息类型["+v+"]不匹配.");
        }
        return null;
    }
    public static DiseaseMsgTypeEnum fromTypeName(String n) {
        if(StringUtils.isNotBlank(n)){
            for (DiseaseMsgTypeEnum c: DiseaseMsgTypeEnum.values()) {
                if (c.name().equals(n)) {
                    return c;
                }
            }
            throw new EnumMatchException("慢病消息类型["+n+"]不匹配.");
        }
        return null;
    }
}

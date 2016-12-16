package com.wondersgroup.healthcloud.services.user.message.enums;

import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import org.apache.commons.lang3.StringUtils;

/**
 * 消息中心-消息类型枚举
 * Created by jialing.yao on 2016-12-13.
 */
public enum MsgTypeEnum {
    /*
        0|系统消息|3.0原有功能
        1|我的咨询|3.0原有功能
        2|家庭消息|4.0功能,原亲情账户消息与家庭消息合并
        3|健康活动（暂忽略）|3.0原功能,合并在系统消息里
        4|健康圈消息|4.0功能
        5|慢病消息|4.0功能
     */
    msgType0("0","系统消息"),
    msgType1("1","我的咨询"),
    msgType2("2","家庭消息"),
    msgType3("3",""),
    msgType4("4","健康圈消息"),
    msgType5("5","慢病消息");


    private final String typeCode;
    private final String typeName;
    MsgTypeEnum(String code,String name) {
        typeCode=code;
        typeName=name;
    }
    public String getTypeCode() {
        return typeCode;
    }
    public String getTypeName() {
        return typeName;
    }

    public static MsgTypeEnum fromTypeCode(String v) {
        if(StringUtils.isNotBlank(v)){
            for (MsgTypeEnum c: MsgTypeEnum.values()) {
                if (c.getTypeCode().equals(v)) {
                    return c;
                }
            }
            throw new EnumMatchException("消息类型["+v+"]不匹配.");
        }
        return null;
    }
    public static MsgTypeEnum fromTypeName(String n) {
        if(StringUtils.isNotBlank(n)){
            for (MsgTypeEnum c: MsgTypeEnum.values()) {
                if (c.name().equals(n)) {
                    return c;
                }
            }
            throw new EnumMatchException("消息类型["+n+"]不匹配.");
        }
        return null;
    }
}

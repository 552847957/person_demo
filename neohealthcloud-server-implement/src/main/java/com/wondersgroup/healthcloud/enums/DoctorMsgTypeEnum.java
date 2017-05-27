package com.wondersgroup.healthcloud.enums;

import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by longshasha on 17/5/19.
 */
public enum DoctorMsgTypeEnum {

    msgType1("1","系统消息"),
    msgType2("2","医生私信"),//不知道是不是医生私信
    msgType3("3","筛查提醒"),
    msgType4("4","干预提醒"),
    msgType5("5","随访提醒");


    private final String typeCode;
    private final String typeName;

    DoctorMsgTypeEnum(String code,String name) {
        typeCode=code;
        typeName=name;
    }

    public String getTypeCode() {
        return typeCode;
    }
    public String getTypeName() {
        return typeName;
    }

    public static DoctorMsgTypeEnum fromTypeCode(String v) {
        if(StringUtils.isNotBlank(v)){
            for (DoctorMsgTypeEnum c: DoctorMsgTypeEnum.values()) {
                if (c.getTypeCode().equals(v)) {
                    return c;
                }
            }
            throw new EnumMatchException("消息类型["+v+"]不匹配.");
        }
        return null;
    }
    public static DoctorMsgTypeEnum fromTypeName(String n) {
        if(StringUtils.isNotBlank(n)){
            for (DoctorMsgTypeEnum c: DoctorMsgTypeEnum.values()) {
                if (c.name().equals(n)) {
                    return c;
                }
            }
            throw new EnumMatchException("消息类型["+n+"]不匹配.");
        }
        return null;
    }
}

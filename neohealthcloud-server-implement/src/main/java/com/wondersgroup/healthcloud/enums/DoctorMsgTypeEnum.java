package com.wondersgroup.healthcloud.enums;

import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by longshasha on 17/5/19.
 */
public enum DoctorMsgTypeEnum {

    msgTypeSys("1","系统消息",""),
    msgType2("2","医生私信",""),//不知道是不是医生私信(产品说他也不知道医生私信是什么==去掉)
    msgTypeQuestion("3","问诊消息","com.wondersgroup.healthcloud.3101://doctor/question?questionId=%s&type=0"),
    msgTypeInterven("4","干预提醒","com.wondersgroup.healthcloud.3101://doctor/intervention_detail?patientId=%s");//测量项目也有用到


    private final String typeCode;
    private final String typeName;
    private final String urlFragment;

    DoctorMsgTypeEnum(String code,String name,String url) {
        typeCode = code;
        typeName = name;
        urlFragment = url;
    }

    public String getTypeCode() {
        return typeCode;
    }
    public String getTypeName() {
        return typeName;
    }
    public String getUrlFragment(){
        return urlFragment;
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

package com.wondersgroup.healthcloud.common.appenum;

import com.wondersgroup.healthcloud.exceptions.CommonException;
import org.apache.commons.lang3.StringUtils;

/**
 * 动态消息类型枚举
 * Created by jialing.yao on 2016-8-18.
 */
public enum DynamicMsgTypeEnum {
    /** 您关注的好友发布了新的话题 **/
    msgType0("0");

    private final String value;
    DynamicMsgTypeEnum(String v) {
        value=v;
    }
    public String value() {
        return value;
    }

    public static DynamicMsgTypeEnum fromValue(String v) {
        if(StringUtils.isNotBlank(v)){
            for (DynamicMsgTypeEnum c: DynamicMsgTypeEnum.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new CommonException(1000, "动态消息类型["+v+"]不匹配.");
        }
        return null;
    }
    public static DynamicMsgTypeEnum fromName(String n) {
        if(StringUtils.isNotBlank(n)){
            for (DynamicMsgTypeEnum c: DynamicMsgTypeEnum.values()) {
                if (c.name().equals(n)) {
                    return c;
                }
            }
            throw new CommonException(1000, "动态消息类型["+n+"]不匹配.");
        }
        return null;
    }
}

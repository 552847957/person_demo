package com.wondersgroup.healthcloud.common.appenum;

/**
 * 消息中心-文案枚举
 * Created by jialing.yao on 2016-8-18.
 */
public enum DynamicMsgContentEnum {
    dynamic_msgType0("您关注的好友发布了新的话题");

    private final String value;
    DynamicMsgContentEnum(String v) {
        value=v;
    }
    public String value() {
        return value;
    }
}

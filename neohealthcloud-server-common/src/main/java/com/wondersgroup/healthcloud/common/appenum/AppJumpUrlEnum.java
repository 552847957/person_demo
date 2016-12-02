package com.wondersgroup.healthcloud.common.appenum;

/**
 * app点击跳转的链接枚举
 * Created by limenghua on 2016/8/30.
 * @author limenghua
 */
public enum AppJumpUrlEnum {
    TOPIC_URL("com.wolf.vaccine://patient/bbs/topic_detail?topic_id=%s");

    private String value;

    AppJumpUrlEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}

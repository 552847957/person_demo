package com.wondersgroup.healthcloud.common.appenum;

import com.wondersgroup.healthcloud.exceptions.CommonException;
import org.apache.commons.lang3.StringUtils;

/**
 * 系统消息类型枚举
 * Created by jialing.yao on 2016-8-18.
 */
public enum SysMsgTypeEnum {
    /*
        0：［有新的粉丝关注了您］
        1：［您的话题“红红火火啊…”被管理员删除了］
        2：［您的话题“红红火火啊…”被管理员加精了］
        3：［您的话题“标题标题育…”有新的评论］
        4：［您在话题“标题标题育…”的评论有新的回复］
        5：［您已被管理员禁言］
        6：［您已被管理员解除禁言］
        7: ［您举报的话题“$话题标题”已被管理员审核通过并删除。］
        8: ［您举报的评论“$评论内容”已被管理员审核通过并删除。］
        9: ［您的评论“【$评论内容】”已被管理员删除。］
     */
    msgType0("0"),
    msgType1("1"),
    msgType2("2"),
    msgType3("3"),
    msgType4("4"),
    msgType5("5"),
    msgType6("6"),
    msgType7("7"),
    msgType8("8"),
    msgType9("9");

    private final String value;
    SysMsgTypeEnum(String v) {
        value=v;
    }
    public String value() {
        return value;
    }

    public static SysMsgTypeEnum fromValue(String v) {
        if(StringUtils.isNotBlank(v)){
            for (SysMsgTypeEnum c: SysMsgTypeEnum.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new CommonException(1000, "系统消息类型["+v+"]不匹配.");
        }
        return null;
    }
    public static SysMsgTypeEnum fromName(String n) {
        if(StringUtils.isNotBlank(n)){
            for (SysMsgTypeEnum c: SysMsgTypeEnum.values()) {
                if (c.name().equals(n)) {
                    return c;
                }
            }
            throw new CommonException(1000, "系统消息类型["+n+"]不匹配.");
        }
        return null;
    }
}

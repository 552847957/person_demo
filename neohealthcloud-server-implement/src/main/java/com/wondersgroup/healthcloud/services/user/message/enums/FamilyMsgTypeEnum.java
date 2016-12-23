package com.wondersgroup.healthcloud.services.user.message.enums;

import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import org.apache.commons.lang3.StringUtils;

/**
 * 家庭消息类型枚举
 * Created by jialing.yao on 2016-12-12.
 */
public enum FamilyMsgTypeEnum {
    /*
        0 A邀请B加入家庭，A为申请消息、B收到邀请消息;（不可点击）
        1 A关闭B用户查阅权限，B收到消息：对方已关闭健康数据查阅权限。（不可点击）
        2 A关闭B用户查阅权限，B向A申请权限，A收到消息：xxx  向你申请开通健康数据查阅权限。（不可点击）
        3 A解除与B用户的关系，B收到消息：xxx  已与你解除绑定关系。（不可点击）
        4 A用户提醒用户B开启 [就医记录] 功能，B收到消息：xxx    提示你，开启就医记录，即刻查看上海市就医记录
        5 A用户提醒用户B开启 [BMI管理] 功能，B收到消息：xxx   提示你，输入身高体重，BMI数值马上知晓
        6 A用户提醒用户B开启 [血压管理] 功能，B收到消息：xxx    提示你，需要管理自己的血压啦
        7 A用户提醒用户B开启 [血糖管理] 功能，B收到消息：xxx    提示你，需要管理自己的血糖啦
        8 A用户提醒用户B开启 [风险评估] 功能，B收到消息：xxx    提示你，做一做风险评估，看看是否有慢病风险哦
        9 A用户提醒用户B开启 [中医体质辨识] 功能，B收到消息：xxx  提示你，做一做中医体质辨识，看看你是属于哪种体质？
        10 A用户提醒用户B开启 [计步管理] 功能，B收到消息：xxx   提示你，一起计步领金币，兑换奖品啦
     */
    msgType0("0","%s","等待对方通过家庭成员申请|请求添加你为家人",""),// 以|为分割符: [0]关系 ,[1]昵称
    msgType1("1","%s","对方已关闭健康数据查阅权限",""),
    msgType2("2","%s","向你申请开通健康数据查阅权限",""),
    msgType3("3","%s","已与你解除绑定关系",""),
    msgType4("4","%s","提示你，开启就医记录，即刻查看上海市就医记录","com.wondersgroup.healthcloud.3101://user/to_verification"),
    msgType5("5","%s","提示你，输入身高体重，BMI数值马上知晓","com.wondersgroup.healthcloud.3101://user/measure/boold_bmi"),
    msgType6("6","%s","提示你，需要管理自己的血压啦","com.wondersgroup.healthcloud.3101://user/measure/boold_pressure"),
    msgType7("7","%s","提示你，需要管理自己的血糖啦","com.wondersgroup.healthcloud.3101://user/measure/boold_pressure"),
    msgType8("8","%s","提示你，做一做风险评估，看看是否有慢病风险哦","com.wondersgroup.healthcloud.3101://user/risk_assessment"),
    msgType9("9","%s","提示你，做一做中医体质辨识，看看你是属于哪种体质？","com.wondersgroup.healthcloud.3101://user/recognition_sign"),
    msgType10("10","%s","提示你，一起计步领金币，兑换奖品啦","com.wondersgroup.healthcloud.3101://user/step_home");


    public final String msgCode;
    public final String msgTitle;
    public final String msgContent;
    public final String msgJumpUrl;
    FamilyMsgTypeEnum(String code, String title, String content, String jumpUrl) {
        msgCode=code;
        msgTitle=title;
        msgContent=content;
        msgJumpUrl=jumpUrl;
    }
    public String getTypeCode() {
        return msgCode;
    }

    public static FamilyMsgTypeEnum fromTypeCode(String v) {
        if(StringUtils.isNotBlank(v)){
            for (FamilyMsgTypeEnum c: FamilyMsgTypeEnum.values()) {
                if (c.getTypeCode().equals(v)) {
                    return c;
                }
            }
            throw new EnumMatchException("家庭消息类型["+v+"]不匹配.");
        }
        return null;
    }
    public static FamilyMsgTypeEnum fromTypeName(String n) {
        if(StringUtils.isNotBlank(n)){
            for (FamilyMsgTypeEnum c: FamilyMsgTypeEnum.values()) {
                if (c.name().equals(n)) {
                    return c;
                }
            }
            throw new EnumMatchException("家庭消息类型["+n+"]不匹配.");
        }
        return null;
    }
}

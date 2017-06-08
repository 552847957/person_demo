package com.wondersgroup.healthcloud.enums;

import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import org.apache.commons.lang3.StringUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by longshasha on 17/5/26.
 * 干预异常类型
 */
public enum IntervenEnum {
    msgType1("10000","血糖首次异常"),
    msgType2("20000","血糖连续7天过高"),
    msgType3("30000","当日首次异常(血糖普通异常)"),//还没有形成血糖连续7天异常的数据
    msgType4("40000","血压首次异常"),
    msgType5("40001","非首次收缩压过高"),
    msgType6("40002","非首次舒张压过低"),
    msgType7("40003","3天收缩压持续升高"),
    msgType8("40004","脉压差异常");


    private final String typeCode;
    private final String typeName;

    IntervenEnum(String code,String name) {
        typeCode=code;
        typeName=name;
    }
    public String getTypeCode() {
        return typeCode;
    }
    public String getTypeName() {
        return typeName;
    }

    public static IntervenEnum fromTypeCode(String v) {
        if(StringUtils.isNotBlank(v)){
            for (IntervenEnum c: IntervenEnum.values()) {
                if (c.getTypeCode().equals(v)) {
                    return c;
                }
            }
            throw new EnumMatchException("干预异常类型["+v+"]不匹配.");
        }
        return null;
    }
    public static IntervenEnum fromTypeName(String n) {
        if(StringUtils.isNotBlank(n)){
            for (IntervenEnum c: IntervenEnum.values()) {
                if (c.name().equals(n)) {
                    return c;
                }
            }
            throw new EnumMatchException("干预异常类型["+n+"]不匹配.");
        }
        return null;
    }

    public static String getIntervenTypeNames(String types){
        if(StringUtils.isNotBlank(types)){
            String[] typeList = types.split(",");
            List<String> data = new ArrayList<String>();
            String result = "";
            for (int i = 0; i < typeList.length; i++) {
                String s = typeList[i];
                if (!data.contains(s)) {
                    data.add(s);
                }
            }
            Collections.sort(data);

            for(String type : data){
                result = result + IntervenEnum.fromTypeCode(type).getTypeName() + ",";
            }
            if(result.length()>0)
                result = result.substring(0,result.length()-1);
            return result;
        }
        return "";

    }

}

package com.wondersgroup.healthcloud.jpa.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 个人健康 状态
 * Created by xianglinhai on 2016/12/15.
 */
public enum UserHealthStatusEnum {

    //健康状态0:无数据 1:良好 2:异常
    HAVE_NO_DATA("0","无数据"),
    HAVE_GOOD_HEALTH("1","良好"),
    HAVE_UNHEALTHY("2","异常");

    UserHealthStatusEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }


    private String id;
    private String name;


    public static UserHealthStatusEnum getEnumById(String id){
        if(StringUtils.isBlank(id)){
            return null;
        }

        for(UserHealthStatusEnum cenum: UserHealthStatusEnum.values()){
            if(id.equals(cenum.id)){
                return cenum;
            }
        }
        return null;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }
}

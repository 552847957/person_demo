package com.wondersgroup.healthcloud.jpa.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 家庭健康 状态
 * Created by xianglinhai on 2016/12/14.
 */
public enum FamilyHealthStatusEnum {

    //健康状态0:无家人 1:有家人家人无数据 2:有家人家人正常 3:异常
    HAVE_NO_FAMILY("0","无家人"),
    HAVE_FAMILY_WITHOUT_DATA("1","有家人家人无数据"),
    HAVE_FAMILY_AND_HEALTHY("2","有家人家人正常"),
    HAVE_FAMILY_AND_UNHEALTHY("3","异常");

    FamilyHealthStatusEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }


    private String id;
    private String name;


    public static FamilyHealthStatusEnum getEnumById(String id){
        if(StringUtils.isBlank(id)){
            return null;
        }

        for(FamilyHealthStatusEnum cenum: FamilyHealthStatusEnum.values()){
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

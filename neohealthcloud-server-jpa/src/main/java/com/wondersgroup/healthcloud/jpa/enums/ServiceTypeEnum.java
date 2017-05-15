package com.wondersgroup.healthcloud.jpa.enums;

/**
 * Created by Administrator on 2017/5/9.
 */
public enum ServiceTypeEnum {
    //服务分类
    DEFAULT_SERVICE(0, "默认服务"),
    BASE_SERVICE(1, "基础服务"),
    SPECIAL_SERVICE(2, "特色服务"),
    MEDICINE_CLOUD_SERVICE(3, "医养云");

    private Integer type;
    private String name;

    ServiceTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static ServiceTypeEnum getNameByType(Integer type) {
        if (null == type) {
            return null;
        }
        for (ServiceTypeEnum cenum : ServiceTypeEnum.values()) {
            if (cenum.type.intValue() == type.intValue()) {
                return cenum;
            }
        }
        return null;
    }

    public Integer getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

}

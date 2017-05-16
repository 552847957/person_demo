package com.wondersgroup.healthcloud.jpa.enums;

/**
 * Created by Administrator on 2017/5/9.
 */
public enum TabServiceTypeEnum {
    //服务分类
    BACKGROUND(0, "背景图片"),
    NO_HIGHTLIGHT(1, "非高亮图标"),
    HIGHTLIGHT(2, "高亮图标");

    private Integer type;
    private String name;

    TabServiceTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static TabServiceTypeEnum getNameByType(Integer type) {
        if (null == type) {
            return null;
        }
        for (TabServiceTypeEnum cenum : TabServiceTypeEnum.values()) {
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

package com.wondersgroup.healthcloud.common.appenum;

/**
 * Created by zhaozhenxing on 2016/6/12.
 */
public enum ImageTextEnum {

    HOME_BANNER("首页Banner", 0),
    HOME_FUNCTION("首页功能图标", 1),
    LOADING_IMAGE("启动页广告", 2),
    NAVIGATION_BAR("导航按钮", 3),
    SERVICE_BTN("服务功能列表", 4),
    HOME_ADVERTISEMENT("首页广告位", 5);


    private String name;
    private Integer type;

    private ImageTextEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static ImageTextEnum fromValue(Integer type) {
        if (type != null) {
            for (ImageTextEnum imageTextEnum : ImageTextEnum.values()) {
                if (imageTextEnum.type.equals(type)) {
                    return imageTextEnum;
                }
            }
        }
        return null;
    }
}

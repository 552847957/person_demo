package com.wondersgroup.healthcloud.jpa.enums;

/**
 * Created by zhuchunliu on 2016/10/21.
 */
public enum  GameType {
    BACTERIA("细菌大作战","bacteria"),
    TURNTABLE("幸运大转盘","turntable"),
    LIGHT("健康点亮活动","light");
    public final String name;
    public final String type;

    private GameType(String name ,String type){
        this.name = name;
        this.type = type;
    }


}

package com.wondersgroup.healthcloud.enums;

import org.apache.commons.lang3.StringUtils;


/**
 * Created by Jeffrey on 16/8/17.
 */
public enum CriterionType {

    ORDINARY("0", "正常"),
    HIGH("1", "偏胖"),
    LOW("2", "偏瘦"),
    HIGHER("3", "肥胖"),
    LOWER("4", "过瘦");
    public final String index;

    public final String name;

    CriterionType(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public static CriterionType parse(String index) {
        if (StringUtils.isBlank(index)) {
            return null;
        }
        for (CriterionType type : values()) {
            if (type.index.equals(index)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant index " + index + "to " + CriterionType.class.getName());
    }
}

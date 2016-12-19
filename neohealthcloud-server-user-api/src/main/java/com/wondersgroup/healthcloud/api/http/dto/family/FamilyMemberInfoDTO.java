package com.wondersgroup.healthcloud.api.http.dto.family;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.api.http.dto.measure.MeasureInfoDTO;

/**
 * 家庭首页个人信息DTO
 * Created by sunhaidi on 2016年12月7日
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberInfoDTO {

    private Info              info;
    private List<InfoTemplet> infoTemplets;

    public static String[]    desc = { "实名制", "就医记录", "家庭医生", "计步管理", "BMI管理", "血糖管理", "血压管理", "风险评估", "中医体质辨识" };

    @Data
    public static class Info {
        private String  id;             //id
        private String  nikcName;      //昵称
        private String  relation_name;  //关系名称
        private Integer age;           //年龄
        private Boolean isVerification; //是否实名
        private Boolean isStandalone;  //是否单机版
        private Boolean access;        //是否有权限
        private String  mobile;
        String          desc;

    }

    @Data
    public static class InfoTemplet {
        int          type;
        String       title;
        String       desc;
        List<MeasureInfoDTO> values;

        public InfoTemplet() {

        }

        public InfoTemplet(int type, String title, List<MeasureInfoDTO> values) {
            this.type = type;
            this.title = title;
            this.values = values;
        }

        public InfoTemplet(int type, String title, String desc, List<MeasureInfoDTO> values) {
            this.type = type;
            this.title = title;
            this.desc = desc;
            this.values = values;
        }
        
    }

    public static class MemberInfoTemplet {
        public static int                 VERIFICATION   = 1;                             //实名制
        public static int                 DOCTOR_RECORD  = 2;                             //就医记录
        public static int                 FAMILY_DOCTOR  = 3;                             //家庭医生
        public static int                 JOGGING        = 4;                             //计步管理
        public static int                 BMI            = 5;                             //BMI管理
        public static int                 BLOODSUGAR     = 6;                             //血糖管理
        public static int                 BLOODPRESSURE  = 7;                             //血压管理
        public static int                 RISKEVALUATE   = 8;                             //风险评估
        public static int                 HEALTHQUESTION = 9;                             //中医体质辨识
        public static int                 CHILD_VACCINE  = 10;                            //儿童疫苗

        public static Map<Integer, String> map            = new TreeMap<Integer, String>();

        static {
            map.put(VERIFICATION, "实名制");
            map.put(DOCTOR_RECORD, "就医记录");
            map.put(FAMILY_DOCTOR, "家庭医生");
            map.put(JOGGING, "计步管理");
            map.put(BMI, "BMI管理");
            map.put(BLOODSUGAR, "血糖管理");
            map.put(BLOODPRESSURE, "血压管理");
            map.put(RISKEVALUATE, "风险评估");
            map.put(HEALTHQUESTION, "中医体质辨识");
            map.put(CHILD_VACCINE, "天后可接种疫苗");
        }

    }
}

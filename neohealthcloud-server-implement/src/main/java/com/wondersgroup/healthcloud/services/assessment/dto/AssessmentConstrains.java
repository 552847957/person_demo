package com.wondersgroup.healthcloud.services.assessment.dto;

/**
 * Created by zhuchunliu on 2016/08/16.
 */
public class AssessmentConstrains {
    /**
     * 标准判断
     */
    public static final Integer CHOISE_TRUE = 1;
    public static final Integer CHOISE_FALSE = 2;
    public static final Integer CHOISE_UNSPECIFIED = 3;

    /**
     * 病史
     *  1.有糖调节受损（IGR，又称糖尿病前期）、
     2.动脉粥样硬化心脑血管疾病、
     3.有一过性类固醇糖尿病、
     4.房颤或明显的脉搏不齐、
     5.短暂脑缺血发作病史（TIA）
     */
    public static final String MEDICAL_HISTORY_IGR="1";
    public static final String MEDICAL_HISTORY_ATHEROSCLEROSIS="2";
    public static final String MEDICAL_HISTORY_STEROID="3";
    public static final String MEDICAL_HISTORY_AF="4";
    public static final String MEDICAL_HISTORY_TIA="5";
    public static final String MEDICAL_HISTORY_NONE="6";


    /**
     * 女性病史
     *  有巨大儿（出生体重>=4KG）生产史
     有妊娠期糖尿病史
     多囊卵巢综合症患者
     以上均无
     */
    public static final String FAMALE_MEDICAL_HISTORY_DELIVERY="1";
    public static final String FAMALE_MEDICAL_HISTORY_GDM="2";
    public static final String FAMALE_MEDICAL_HISTORY_PCOS="3";
    public static final String FAMALE_MEDICAL_HISTORY_NONE="4";

    /**
     * 运动情况
     */
    public static final Integer SPORT_EVERYDAY = 1;
    public static final Integer SPORT_ONEWEEK = 2;
    public static final Integer SPORT_ATTIMES = 3;
    public static final Integer SPORT_NONE = 4;

    /**
     * 饮食习惯
     */
    public static final Integer EAT_HABITS_BALANCED = 1;
    public static final Integer EAT_HABITS_ANIMAL = 2;
    public static final Integer EAT_HABITS_VEGETARIAN = 3;

    /**
     * 饮食口味
     */
    public static final String EAT_TASTE_SALT = "1";
    public static final String EAT_TASTE_OIL = "2";
    public static final String EAT_TASTE_SUGAR = "3";
    public static final String EAT_TASTE_NORMAL = "4";

    /**
     * 饮酒
     */
    public static final Integer DRINK_ATTIMES = 1;
    public static final Integer DRINK_OFTEN = 2;
    public static final Integer DRINK_EVERYDAY = 3;
    public static final Integer DRINK_NONE = 4;

    /**
     * 吸烟
     */
    public static final Integer SMOKE_EVERYDAY = 1;
    public static final Integer SMOKE_NOTEVERYDAY = 2;
    public static final Integer SMOKE_AGO = 3;
    public static final Integer SMOKE_NONE = 4;

    /**
     * 亲属
     * 本人、子、女、父亲、母亲、兄弟姐妹、祖父母或外祖父母
     */
    public static final String RELATIVES_NONE = "0";
    public static final String RELATIVES_ONESELF = "1";
    public static final String RELATIVES_SON = "2";
    public static final String RELATIVES_DAUGHTER = "3";
    public static final String RELATIVES_DAD = "4";
    public static final String RELATIVES_MOM = "5";
    public static final String RELATIVES_BRANDSR = "6";
    public static final String RELATIVES_GPANDGM = "7";

    /**正常**/
    public static final String MEASURE_FLAG_NORMAL = "0";
    /**偏低**/
    public static final String MEASURE_FLAG_LOW = "1";
    /**偏高**/
    public static final String MEASURE_FLAG_HIGH = "2";
    /**超高**/
    public static final String MEASURE_FLAG_ULTRAHIGH = "3";

    /**
     * 性别
     */
    public static final String GENDER_MAN = "1";
    public static final String GENDER_WOMAN = "2";
}

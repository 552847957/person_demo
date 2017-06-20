package com.wondersgroup.healthcloud.services.interven.entity;

import com.wondersgroup.healthcloud.utils.IdcardUtils;
import lombok.Data;

import java.util.Date;

/**
 * Created by longshasha on 17/5/26.
 */
@Data
public class IntervenEntity {

    private String name;//姓名
    private String gender;//性别 1 男 2 女
    private Integer age;//年龄
    private String avatar;//头像

    private String identifytype;
    private String register_id;//如果C端实名认证过的用户有registerId
    private String is_risk;//
    private String hyp_type;//是否是高血压 "高"
    private String diabetes_type;//是否是糖尿病 "糖"
    private String apo_type;//是否是脑卒中 "脑"

    private String  sign_status;//'签约状态 1：已签约,0:未签约',

    private String typelist;//异常类型 10000,20000

    private String personcard;//身份证号


    /**
     * 我的干预列表
     */
    private String id;
    private Date interventionDate;
    private String content;


    public Integer getAge() {
        return IdcardUtils.getAgeByIdCard(this.personcard);
    }
}

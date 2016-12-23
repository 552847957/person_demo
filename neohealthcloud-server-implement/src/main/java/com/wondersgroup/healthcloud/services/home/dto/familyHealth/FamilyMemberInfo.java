package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import lombok.Data;

import java.util.Date;

/**
 * 家庭成员信息
 * Created by xianglinhai on 2016/12/23.
 */
@Data
public class FamilyMemberInfo {

    private String uid;      //id

    private String personCard;   //身份证号码

    private String gender;   //性别

    private Date birthday;   //生日

    private String  relation;    //成员与申请人关系',



}

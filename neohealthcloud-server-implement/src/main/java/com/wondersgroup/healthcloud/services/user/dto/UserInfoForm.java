package com.wondersgroup.healthcloud.services.user.dto;

import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;

/**
 * Created by longshasha on 16/8/9.
 */
public class UserInfoForm {

    public String registerId;

    public Integer age;

    public Integer height;

    public Float weight;

    public Float waist;

    public String gender;


    public UserInfoForm() {
    }

    public UserInfo merge(UserInfo userInfo){
        if(age !=null){
            userInfo.setAge(age);
        }
        if(height!=null){
            userInfo.setHeight(height);
        }

        if(weight!=null){
            userInfo.setWeight(weight);
        }

        if(waist !=null){
            userInfo.setWaist(waist);
        }

        return  userInfo;
    }

}

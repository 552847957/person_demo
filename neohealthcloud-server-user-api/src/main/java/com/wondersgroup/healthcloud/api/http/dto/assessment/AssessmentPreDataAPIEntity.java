package com.wondersgroup.healthcloud.api.http.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

/**
 * Created by zhuchunliu on 2015/12/31.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentPreDataAPIEntity {

    private String year;
    private String month;
    private String gender;


    public AssessmentPreDataAPIEntity(RegisterInfo register) {
        this.gender = register.getGender();
        if(!StringUtils.isEmpty(register.getPersoncard()) && 18 == register.getPersoncard().length()){
            this.year = register.getPersoncard().substring(6,10);
            this.month = Integer.parseInt(register.getPersoncard().substring(10,12))+"";
        }else if(null != register.getBirthday()){
            this.year = new DateTime(register.getBirthday()).getYear()+"";
            this.month = new DateTime(register.getBirthday()).getMonthOfYear()+"";
        }

    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

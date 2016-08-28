package com.wondersgroup.healthcloud.api.http.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Created by zhuchunliu on 2015/12/31.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentPreDataAPIEntity {

    private Integer age;
    private Integer height;
    private Integer weight;
    private String pressure;

    public AssessmentPreDataAPIEntity(RegisterInfo register,UserInfo userInfo) {
        if(null != register && null != register.getPersoncard()){
            Date birthday = DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(register.getPersoncard()));
            this.age = new DateTime().getYear() - new DateTime(birthday).getYear();
        }else if(null != userInfo){
            this.age = userInfo.getAge();
        }else if(null != register && null != register.getBirthday()){
            this.age = new DateTime().getYear() - new DateTime(register.getBirthday()).getYear();
        }
        if(null != userInfo){
            this.height = userInfo.getHeight();
            if(null != userInfo.getWeight()){
                this.weight = Integer.parseInt(String.format("%.0f", userInfo.getWeight()));
            }
        }

    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}

package com.wondersgroup.healthcloud.api.http.dto.doctor.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.Map;

/**
 * 随访提醒
 * Created by zhuchunliu on 2017/5/24.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FollowRemindDto {
    @JsonProperty(value="registerId")
    private String registerid;//用户主键
    @JsonProperty(value="avatar")
    private String headphoto;// 头像
    private String name;//姓名
    private String gender;//性别
    private Integer age;//年龄
    @JsonProperty(value="identifyType")
    private Boolean hasIdentify;// 是否实名认证
    @JsonProperty(value="isRisk")
    private Boolean hasHignRisk;//是否高危
    @JsonProperty(value="diabetesType")
    private Boolean hasDiabetes;//是否糖尿病
    @JsonProperty(value="hypType")
    private Boolean hasBloodPressure;//是否高血压
    @JsonProperty(value="apoType")
    private Boolean hasStroke;//是否脑卒中
    private String followBeginDate;//随访开始时间
    private String followEndDate;//随访结束时间

    public FollowRemindDto(Map<String, Object> map,  RegisterInfo register, UserInfo userInfo) {
        this.registerid = map.get("registerid").toString();
        this.headphoto = StringUtils.isEmpty(register.getHeadphoto())?"":register.getHeadphoto()+ ImagePath.avatarPostfix();
        this.name = register.getName();
        this.gender = register.getGender();
        this.hasIdentify = "0".equals(register.getIdentifytype()) ?false:true;
        this.hasHignRisk = null == map.get("is_risk") || "0".equals(map.get("is_risk").toString()) ?false:true;;
        this.hasDiabetes = null == map.get("diabetes_type") || "0".equals(map.get("diabetes_type").toString()) ?false:true;
        this.hasBloodPressure = null == map.get("hyp_type") || "0".equals(map.get("hyp_type").toString()) ?false:true;
        this.hasStroke = null == map.get("apo_type") || "0".equals(map.get("apo_type").toString()) ?false:true;

        if(null != register && null != register.getPersoncard()){
            Date birthday = DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(register.getPersoncard()));
            this.age = new DateTime().getYear() - new DateTime(birthday).getYear();
        }else if(null != userInfo){
            this.age = userInfo.getAge();
        }else if(null != register && null != register.getBirthday()){
            this.age = new DateTime().getYear() - new DateTime(register.getBirthday()).getYear();
        }
        if(null != map.get("follow_date")){
            this.followBeginDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S").parseDateTime(map.get("follow_date").toString()).toString("yyyy-MM-dd");
        }

        if(null != map.get("remind_end_date")){
            this.followEndDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S").parseDateTime(map.get("remind_end_date").toString()).toString("yyyy-MM-dd");
        }

    }
}

package com.wondersgroup.healthcloud.api.http.dto.doctor.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
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
 * Created by zhuchunliu on 2017/5/24.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FollowRemindMineDto {
    private String headphoto;// 头像
    private String name;//姓名
    private String gender;//性别
    private Integer age;//年龄
    private Boolean hasIdentify;// 是否实名认证
    private Boolean hasHignRisk;//是否高危
    private Boolean hasDiabetes;//是否糖尿病
    private Boolean hasBloodPressure;//是否高血压
    private Boolean hasStroke;//是否脑卒中
    private String followDate;//随访开始时间

    public FollowRemindMineDto(Map<String, Object> map,  RegisterInfo register, UserInfo userInfo) {
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
        if(null != map.get("report_date")){
            this.followDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S").parseDateTime(map.get("report_date").toString()).toString("yyyy-MM-dd");
        }

    }
}

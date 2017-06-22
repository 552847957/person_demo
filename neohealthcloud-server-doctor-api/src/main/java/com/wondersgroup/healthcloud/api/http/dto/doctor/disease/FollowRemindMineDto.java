package com.wondersgroup.healthcloud.api.http.dto.doctor.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.services.doctor.dto.BaseResidentDto;
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
public class FollowRemindMineDto extends BaseResidentDto {
    private String followDate;//随访开始时间
    private String followDetailUrl;//随访详情

    public FollowRemindMineDto(Map<String, Object> map,String doctorCard,String baseUrl) {

        this.setRegisterId(map.get("registerid").toString());
        this.setAge(null == map.get("age")?null:Integer.parseInt(map.get("age").toString()));
        this.setAvatar(null == map.get("avatar")?null:map.get("avatar").toString());
        this.setName(null == map.get("name")?null:map.get("name").toString());
        this.setGender(null == map.get("gender")?null:map.get("gender").toString());
        this.setIdentifyType(null == map.get("identifytype") || "0".equals(map.get("identifytype").toString()) ?false:true);
        this.setIsRisk(null == map.get("is_risk") || "0".equals(map.get("is_risk").toString()) ?false:true);
        this.setDiabetesType(null == map.get("diabetes_type") || "0".equals(map.get("diabetes_type").toString()) ?false:true);
        this.setHypType(null == map.get("hyp_type") || "0".equals(map.get("hyp_type").toString()) ?false:true);
        this.setApoType(null == map.get("apo_type") || "0".equals(map.get("apo_type").toString()) ?false:true);
        if(null != map.get("sign_status") && "1".equals(map.get("sign_status").toString()) &&
                null != map.get("sign_doctor_personcard") && doctorCard.equals(map.get("sign_doctor_personcard").toString())){
            this.setSignStatus(true);
        }else{
            this.setSignStatus(false);
        }
        if(null != map.get("report_date")){
            this.followDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S").parseDateTime(map.get("report_date").toString()).toString("yyyy-MM-dd");
        }
        this.followDetailUrl = baseUrl+"/FollowUpReport/"+this.getRegisterId()+"/"+this.followDate;

    }
}

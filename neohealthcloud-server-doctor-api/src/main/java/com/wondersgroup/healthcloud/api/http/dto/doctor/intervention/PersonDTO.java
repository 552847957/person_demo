package com.wondersgroup.healthcloud.api.http.dto.doctor.intervention;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.enums.IntervenEnum;
import com.wondersgroup.healthcloud.services.doctor.dto.BaseResidentDto;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by longshasha on 17/5/18.
 * 列表用户DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PersonDTO{

    private String name;//姓名
    private String gender;//性别 1 男 2 女
    private Integer age;//年龄
    private String avatar;//头像

    private String registerId;//如果C端实名认证过的用户有registerId
    /**
     * 是否是"危" 危“是指用户已经实名的情况下，在风险评估中检测下来可能是（糖尿病、脑卒中的患者或高血压的易患人群）
     * （记录用户风险评估的最后一次状态，新检测的结果替换服务器的数据，以最后一次为准！）
     * 如果有高糖脑 那就没有危标签
     */
    @JsonProperty("isRisk")
    private Boolean isRisk;//

    @JsonProperty("identifyType")
    private Boolean isJky;//是否是 "实" 健康云注册并实名认证的
    @JsonProperty("hypType")
    private Boolean isHyp;//是否是高血压 "高"

    @JsonProperty("diabetesType")
    private Boolean isDiabetes;//是否是糖尿病 "糖"

    @JsonProperty("apoType")
    private Boolean isApo;//是否是脑卒中 "脑"

    private String memo;//列表下描述 例:血糖首次异常、血糖连续7天过高

    private Boolean canIntervene;//能否干预

    /**
     * 我的干预列表使用以下字段
     */
    private String id;
    private String interventionTime;
    private String interventionDate;
    private String content;

    public PersonDTO (IntervenEntity intervenEntity){

        if(intervenEntity!=null){
            this.registerId = intervenEntity.getRegister_id()==null?"":intervenEntity.getRegister_id();
            this.name = intervenEntity.getName()==null?"":intervenEntity.getName();
            this.gender = intervenEntity.getGender() == null?"":intervenEntity.getGender();
            this.age = intervenEntity.getAge();
            this.avatar = intervenEntity.getAvatar() == null?"":intervenEntity.getAvatar();
            this.isRisk = StringUtils.isBlank(intervenEntity.getIs_risk())?false:"1".equals(intervenEntity.getIs_risk());
            this.isJky = StringUtils.isBlank(intervenEntity.getIdentifytype())?false:!"0".equals(intervenEntity.getIdentifytype());
            this.isHyp = StringUtils.isBlank(intervenEntity.getHyp_type())?false:!"0".equals(intervenEntity.getHyp_type());
            this.isDiabetes = StringUtils.isBlank(intervenEntity.getDiabetes_type())?false:!"0".equals(intervenEntity.getDiabetes_type());
            this.isApo = StringUtils.isBlank(intervenEntity.getApo_type())?false:!"0".equals(intervenEntity.getApo_type());

            this.canIntervene = this.isJky;//目前的规则是只有在C端有账号的可以进行干预

            this.memo = IntervenEnum.getIntervenTypeNames(intervenEntity.getTypelist());

            this.id = intervenEntity.getId();
            Date intervenDate = intervenEntity.getInterventionDate();
            if(intervenDate!=null){
                this.interventionTime = DateFormatter.dateTim2eFormat(intervenDate);
                this.interventionDate = DateFormatter.dateFormat(intervenDate);
            }
            this.content = intervenEntity.getContent();
        }

    }

}

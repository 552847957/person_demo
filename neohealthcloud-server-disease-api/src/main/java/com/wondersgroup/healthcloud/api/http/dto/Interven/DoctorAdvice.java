package com.wondersgroup.healthcloud.api.http.dto.Interven;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.enums.IntervenEnum;
import com.wondersgroup.healthcloud.services.interven.dto.OutlierDTO;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * Created by longshasha on 17/6/26.
 */
@Data
public class DoctorAdvice {

    //干预id
    private String id;

    @JsonProperty("interven_type")
    private String intervenType;

    @JsonProperty("intervene_time")
    private String interveneTime;// 干预时间（创建时间）

    @JsonProperty("doctor_suggest")
    private String doctorSuggest;// 医生建议内容

    @JsonProperty("doctor_name")
    private String doctorName;

    @JsonProperty("hospital_name")
    private String hospitalName;

    private String avatar;

    @JsonProperty("bloodGlucose_list")
    private List<OutlierDTO> bloodGlucoseList;

    @JsonProperty("pressure_list")
    private List<OutlierDTO> pressureList;

    public DoctorAdvice(IntervenEntity intervenEntity) {

        this.id = intervenEntity.getId();
        this.intervenType = IntervenEnum.getIntervenTypeNames(intervenEntity.getTypelist());
        Date intervenDate = intervenEntity.getInterventionDate();
        if(intervenDate!=null){
            this.interveneTime = DateFormatter.dateFormat(intervenDate);
        }
        this.doctorSuggest = intervenEntity.getContent();
        this.doctorName = intervenEntity.getDoctorName();
        this.hospitalName = intervenEntity.getHospitalName();
        this.avatar = intervenEntity.getDoctorAvatar();

    }
}

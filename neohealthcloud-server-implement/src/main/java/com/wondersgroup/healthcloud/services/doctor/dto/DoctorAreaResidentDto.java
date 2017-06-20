package com.wondersgroup.healthcloud.services.doctor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * Created by limenghua on 2017/6/20.
 * 医生管辖区域的用户查询
 *
 * @author limenghua
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class DoctorAreaResidentDto {
    private String doctorId;
    private String hospitalId;
    private String addressCounty;
    private String registerId;
    private String personCard;

    public DoctorAreaResidentDto() {
    }

    public DoctorAreaResidentDto(String doctorId, String hospitalId, String addressCounty, String registerId, String personCard) {
        this.doctorId = doctorId;
        this.hospitalId = hospitalId;
        this.addressCounty = addressCounty;
        this.registerId = registerId;
        this.personCard = personCard;
    }
}

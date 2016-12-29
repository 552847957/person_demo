package com.wondersgroup.healthcloud.api.http.dto.doctor;

import lombok.Data;

/**
 * Created by zhaozhenxing on 2016/12/22.
 */
@Data
public class DoctorInterventionDTO {
    private String registerId;
    private String name;
    private String sex;
    private String age;
    private int existFirst;
    private int existSeven;
    private String fpgValue;
    private String testTime;
    private String testPeriod;
    private String type;
}

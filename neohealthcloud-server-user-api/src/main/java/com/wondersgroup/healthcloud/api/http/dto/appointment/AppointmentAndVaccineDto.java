package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
/**
 * 
 * @author zhongshuqing
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentAndVaccineDto {
    private String title;
    private String jumpUrl;
    private String img;
}

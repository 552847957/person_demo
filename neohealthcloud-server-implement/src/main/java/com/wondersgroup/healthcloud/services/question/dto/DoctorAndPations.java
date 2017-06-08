package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by Administrator on 2017/5/26.
 */
public class DoctorAndPations {
    @JsonIgnoreProperties(value={"sortDate"})
    public Date sortDate;
}

package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 用户住院记录
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserHealthRecordDTO {
    private String date;
    private String hospital_name;
    private String office_name;
    private String office_type;
    private String diagnose_result;

}

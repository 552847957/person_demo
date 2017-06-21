package com.wondersgroup.healthcloud.services.sign;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.services.doctor.dto.BaseResidentDto;
import lombok.Data;

/**
 * Created by ZZX on 2017/6/9.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignDTO extends BaseResidentDto {
    //是否分组
    private Boolean ifGrouped;
}

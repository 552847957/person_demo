package com.wondersgroup.healthcloud.services.disease.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.services.doctor.dto.BaseResidentDto;
import lombok.Data;

/**
 * Created by limenghua on 2017/6/6.
 *
 * @author limenghua
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResidentInfoDto extends BaseResidentDto {
    //是否分组
    private Boolean ifGrouped;
    private Boolean ifSigned;

    @Override
    public String toString() {
        return "ResidentInfoDto{" +
                "ifGrouped=" + ifGrouped +
                "} " + super.toString();
    }
}

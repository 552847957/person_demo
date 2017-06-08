package com.wondersgroup.healthcloud.api.http.dto.doctor.intervention;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by longshasha on 17/6/7.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class IntervenDetailDTO {

    @JsonProperty("user_info")
    private PersonDTO personDTO;

    private Boolean bloodGlucose_more = false;

    private Boolean pressure_more = false;

    @JsonProperty("bloodGlucose_list")
    private List<OutlierDTO> bloodGlucoseList;

    @JsonProperty("pressure_list")
    private List<OutlierDTO> pressureList;


}

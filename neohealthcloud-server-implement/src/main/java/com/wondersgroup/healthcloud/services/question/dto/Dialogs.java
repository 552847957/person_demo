package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Dialogs {
    private Integer isCurrentDoctor = 0;
    private List dialogDetails = new ArrayList();

}

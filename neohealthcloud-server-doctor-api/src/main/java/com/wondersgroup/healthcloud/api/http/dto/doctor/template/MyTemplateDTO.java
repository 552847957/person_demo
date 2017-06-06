package com.wondersgroup.healthcloud.api.http.dto.doctor.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyTemplateDTO{
    private int currentIndex;//当前数量
    private int  totalCount;//总数量
    private List<TemplateDTO> lastUsed;
    private List<TemplateDTO> templates;

}
package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.api.utls.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 高危筛查
 * Created by Administrator on 2016/12/8.
 */
@RestController
@RequestMapping(value = "/api/screening")
public class RiskScreeningController {

    @Autowired
    private DiabetesAssessmentService assessmentService;

    /**
     * 高危筛查列表
     * @return
     */
    @PostMapping
    public Pager list(@RequestBody Pager pager) {

        return pager;
    }
}

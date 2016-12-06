package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 风险评估
 * 风险评估
 * Created by zhuchunliu on 2016/12/6.
 */
@RestController
@RequestMapping(value = "/api/assessment")
public class DiabetesAssessmentController {

    @Autowired
    private DiabetesAssessmentService assessmentService;

    /**
     * 患病风险评估
     * @param assessment
     * @return
     */
    @RequestMapping(value = "/sicken", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity sicken(@RequestBody DiabetesAssessment assessment) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int result = assessmentService.sicken(assessment);
        entity.setData(ImmutableMap.of("prompt",result == 0?"正常,继续保持":"您已经属于高危人群"));
        return entity;
    }

    /**
     * 肾病风险评估
     * @param assessment
     * @return
     */
    @RequestMapping(value = "/kidney", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity kidney(@RequestBody DiabetesAssessment assessment) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int result = assessmentService.kidney(assessment);
        return entity;
    }

    /**
     * 眼病风险评估
     * @param assessment
     * @return
     */
    @RequestMapping(value = "/eye", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity eye(@RequestBody DiabetesAssessment assessment) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int result = assessmentService.eye(assessment);
        return entity;
    }

    /**
     * 足部风险评估
     * @param assessment
     * @return
     */
    @RequestMapping(value = "/foot", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity foot(@RequestBody DiabetesAssessment assessment) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int result = assessmentService.foot(assessment);
        return entity;
    }

}

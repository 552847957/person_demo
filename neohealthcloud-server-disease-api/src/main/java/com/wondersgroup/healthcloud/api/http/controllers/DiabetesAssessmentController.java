package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
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
        entity.setData(ImmutableMap.of("result",result == 0?"您的评估结果无糖尿病风险，请继续保持":"您属于糖尿病高危人群，请到医院进一步确诊"));
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
        switch (result){
            case 0:
                entity.setData(ImmutableMap.of("result","您目前尚未出现肾脏病变症状，继续保持"));
                break;
            case 1:
                entity.setData(ImmutableMap.of("result","您目前存在一些类似糖尿病肾脏病变的症状或危险因素，请您在日常的生活中多多注意"));
            default:
                entity.setData(ImmutableMap.of("result","您很有可能已经患有糖尿病肾病了，建议您及时咨询您的家庭医生获得专业建议"));
        }
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
        entity.setData(ImmutableMap.of("result",result == 0?"恭喜您，暂未出现糖尿病眼病的症状，请您继续保持":"您目前出现一部分糖尿病眼部症状，请在线咨询您的家庭医生获得专业意见"));
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
        switch (result){
            case 0:
                entity.setData(ImmutableMap.of("result","您不属于糖尿病足的高危人群，恭喜您，请继续保持"));
                break;
            case 1:
                entity.setData(ImmutableMap.of("result","您属于轻度糖尿病足的高危人群，糖尿病足并非微不“足”道，请咨询您的家庭医生获得专业意见"));
            default:
                entity.setData(ImmutableMap.of("result","您属于高度糖尿病足的高危人群，糖尿病足并非微不“足”道，请咨询您的家庭医生获得专业意见"));
        }
        return entity;
    }

}

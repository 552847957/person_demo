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
        entity.setData(ImmutableMap.of("result",result == 0?"您的评估结果无糖尿病风险，请继续保持":"您属于糖尿病高危风险人群，请到医院进一步确诊"));
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
        entity.setData(ImmutableMap.of("result",result == 0?"您本次评估结果尚无糖尿病肾病风险，请继续维持健康的生活方式，并定期体检。":
                "您本次评估结果具有糖尿病肾病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。"));
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
        entity.setData(ImmutableMap.of("result",result == 0?"您本次评估结果尚无糖尿病眼病风险，请继续维持健康的生活方式，并定期体检。":
                "您本次评估结果具有糖尿病眼病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。"));
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
        entity.setData(ImmutableMap.of("result",result == 0?"您本次评估结果尚无糖尿病足病风险，请继续维持健康的生活方式，并定期体检。":
                "您本次评估结果具有糖尿病足病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。"));
        return entity;
    }

}

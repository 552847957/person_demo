package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 风险评估
 * Created by zhuchunliu on 2016/12/6.
 */
@RestController
@RequestMapping(value = "/api/assessment")
public class DiabetesAssessmentController {

    @Autowired
    private DiabetesAssessmentService diabetesAssessmentService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private DiabetesAssessmentRepository assessmentRepo;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    /**
     * 肾病风险评估
     *
     * @param assessment
     * @return
     */
    @RequestMapping(value = "/kidney", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity kidney(@RequestBody DiabetesAssessment assessment) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int result = diabetesAssessmentService.kidney(assessment);
        entity.setData(ImmutableMap.of("result", result == 0 ? "您本次评估结果尚无糖尿病肾病风险，请继续维持健康的生活方式，并定期体检。" :
                "您本次评估结果具有糖尿病肾病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。"));
        return entity;
    }

    /**
     * 眼病风险评估
     *
     * @param assessment
     * @return
     */
    @RequestMapping(value = "/eye", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity eye(@RequestBody DiabetesAssessment assessment) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int result = diabetesAssessmentService.eye(assessment);
        entity.setData(ImmutableMap.of("result", result == 0 ? "您本次评估结果尚无糖尿病眼病风险，请继续维持健康的生活方式，并定期体检。" :
                "您本次评估结果具有糖尿病眼病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。"));
        return entity;
    }

    /**
     * 足部风险评估
     *
     * @param assessment
     * @return
     */
    @RequestMapping(value = "/foot", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity foot(@RequestBody DiabetesAssessment assessment) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int result = diabetesAssessmentService.foot(assessment);
        entity.setData(ImmutableMap.of("result", result == 0 ? "您本次评估结果尚无糖尿病足病风险，请继续维持健康的生活方式，并定期体检。" :
                "您本次评估结果具有糖尿病足病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。"));
        return entity;
    }

    /**
     * 统计每个人的各类型风险评估数量
     *
     * @param registerid
     * @return
     */
    @RequestMapping(value = "/history/num", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponseEntity historyNum(@RequestParam(name = "registerid") String registerid) {
        JsonResponseEntity entity = new JsonResponseEntity();
        int assessmentNum = assessmentService.getAssessNum(registerid);
        List<Map<String,Object>> list = diabetesAssessmentService.getNumByTypeAndRegisterid(registerid);
        int kidneyNum =0,eyeNum=0,footNum = 0 ;
        for(Map<String,Object> map : list){
            if(map.get("type").toString().equals("2")){
                kidneyNum = Integer.parseInt(map.get("num").toString());
            }
            if(map.get("type").toString().equals("3")){
                eyeNum = Integer.parseInt(map.get("num").toString());
            }
            if(map.get("type").toString().equals("4")){
                footNum = Integer.parseInt(map.get("num").toString());
            }

        }
//        int kidneyNum = assessmentRepo.getNumByTypeAndRegisterid(registerid, 2);
//        int eyeNum = assessmentRepo.getNumByTypeAndRegisterid(registerid, 3);
//        int footNum = assessmentRepo.getNumByTypeAndRegisterid(registerid, 4);

        entity.setData(ImmutableMap.of("assessmentNum", assessmentNum, "kidneyNum", kidneyNum, "eyeNum", eyeNum, "footNum", footNum));
        return entity;
    }

    /**
     * 统计每个人的各类型风险评估数量
     *
     * @param registerid
     * @return
     */
    @RequestMapping(value = "/history/list", method = RequestMethod.GET)
    @ResponseBody
    public JsonListResponseEntity historyList(
            @RequestParam(name = "registerid") String registerid,
            @RequestParam(name = "type") Integer type,
            @RequestParam(required = false, defaultValue = "1") Integer flag) {

        JsonListResponseEntity entity = new JsonListResponseEntity();
        int pageSize = 5;


        List<DiabetesAssessment> assessmentList = diabetesAssessmentService.getAssessmentList(registerid,type,flag,pageSize);
        int total = assessmentRepo.getNumByTypeAndRegisterid(registerid,type);

        RegisterInfo register = registerInfoRepo.findOne(registerid);

        List<Map<String,String>> list = Lists.newArrayList();
        for(DiabetesAssessment assessment : assessmentList) {
            Map<String, String> map = Maps.newHashMap();
            map.put("userName", null == register?"":(!StringUtils.isEmpty(register.getName())?register.getName():register.getNickname()));
            map.put("time", new DateTime(assessment.getCreateDate()).toString("yyyy-MM-dd HH:mm:ss"));
            map.put("result", assessment.getResult().toString());
            switch (type) {
                case 2:
                    map.put("resultDoc", 0 == assessment.getResult() ? "无糖尿病肾病风险" : "有糖尿病肾病风险");
                    break;
                case 3:
                    map.put("resultDoc", 0 == assessment.getResult() ? "无糖尿病眼病风险" : "有糖尿病眼病风险");
                    break;
                case 4:
                    map.put("resultDoc", 0 == assessment.getResult() ? "无糖尿病足病风险" : "有糖尿病足病风险");
                    break;
            }

            switch (type) {
                case 2:
                    map.put("advice", 0 == assessment.getResult() ? "您本次评估结果尚无糖尿病肾病风险，请继续维持健康的生活方式，并定期体检。" :
                            "您本次评估结果具有糖尿病肾病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。");
                    break;
                case 3:
                    map.put("advice", 0 == assessment.getResult() ? "您本次评估结果尚无糖尿病眼病风险，请继续维持健康的生活方式，并定期体检。" :
                            "您本次评估结果具有糖尿病眼病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。");
                    break;
                case 4:
                    map.put("advice", 0 == assessment.getResult() ? "您本次评估结果尚无糖尿病足病风险，请继续维持健康的生活方式，并定期体检。" :
                            "您本次评估结果具有糖尿病足病风险，建议您到居住地所属社区卫生服务中心进行并发症筛查，及早控制病情。");
                    break;


            }
            list.add(map);
        }
        boolean hasMore = false;
        if(total > pageSize * flag){
            hasMore = true;
            flag++;
        }
        entity.setContent(list,hasMore,null,flag.toString());
        return entity;
    }

}

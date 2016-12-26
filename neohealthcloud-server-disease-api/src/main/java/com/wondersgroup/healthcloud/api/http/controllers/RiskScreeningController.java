package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.RiskScreeningEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRepository;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import com.wondersgroup.healthcloud.services.diabetes.dto.DiabetesAssessmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 高危筛查
 * Created by Administrator on 2016/12/8.
 */
@RestController
@RequestMapping(value = "/api/screening")
public class RiskScreeningController {

    @Autowired
    private DiabetesAssessmentService assessmentService;


    @Autowired
    private DiabetesAssessmentRepository assessmentRepo;

    /**
     * 高危筛查列表
     * @return
     */
    @GetMapping("/list")
    public JsonListResponseEntity list(
            @RequestParam(required = false) String  name,
            @RequestParam(required = false, defaultValue = "1") Integer flag) {


        int pageSize = 10;
        List<DiabetesAssessmentDTO> list = assessmentService.findAssessment(flag,pageSize,name);
        List<RiskScreeningEntity> entityList = Lists.newArrayList();
        for(DiabetesAssessmentDTO assessment : list){
            entityList.add(new RiskScreeningEntity(assessment));
        }
        Integer total = assessmentService.findAssessmentTotal(name);
        boolean hasMore = false;
        if(total > pageSize * flag){
            hasMore = true;
            flag++;
        }
        JsonListResponseEntity response = new JsonListResponseEntity();
        response.setContent(entityList,hasMore,null,flag.toString());
        return response;
    }

    /**
     * 高危筛查待提醒总数
     * @return
     */
    @GetMapping("/total")
    public JsonResponseEntity list(@RequestParam(required = false) String  name) {
        Integer total = assessmentService.findAssessmentTotal(name);
        return new JsonResponseEntity(0,null, ImmutableMap.of("total",total));
    }

    /**
     * 高危提醒
     * @return
     */
    @PostMapping("/remind")
    public JsonResponseEntity remind(@RequestBody String request) {

        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String ids = reader.readString("ids",false);
        String doctorId = reader.readString("doctorId",false);

        List<String> registerIds = assessmentRepo.findRemidRegisterById(ids.split(","));
        if(0 == registerIds.size() && 1 == ids.split(",").length){
            entity.setCode(1002);
            entity.setMsg("该用户当日已经被提醒");
            return entity;
        }

        Boolean flag = assessmentService.remind(registerIds,doctorId);

        if(flag){
            entity.setMsg("您的糖尿病高危筛查提醒已经发送成功");
        }else{
            entity.setCode(1001);
            entity.setMsg("您的糖尿病高危筛查提醒发送失败");
        }
        return entity;
    }
}

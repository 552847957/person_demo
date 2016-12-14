package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.RiskScreeningEntity;
import com.wondersgroup.healthcloud.api.utls.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import com.wondersgroup.healthcloud.services.diabetes.dto.DiabetesAssessmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 高危筛查列表
     * @return
     */
    @PostMapping("/list")
    public Pager list(@RequestBody Pager pager) {
        List<DiabetesAssessmentDTO> list = assessmentService.findAssessment(pager.getNumber(),pager.getSize(),pager.getParameter());
        List<RiskScreeningEntity> entityList = Lists.newArrayList();
        for(DiabetesAssessmentDTO assessment : list){
            entityList.add(new RiskScreeningEntity(assessment));
        }
        Integer total = assessmentService.findAssessmentTotal(pager.getParameter());
        pager.setData(entityList);
        pager.setTotalElements(total);
        return pager;
    }

    /**
     * 高危提醒
     * @return
     */
    @PostMapping("/remind")
    public JsonResponseEntity remind(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String ids = reader.readString("ids",false);
        String doctorId = reader.readString("doctorId",false);
        Boolean flag = assessmentService.remind(ids,doctorId);
        JsonResponseEntity entity = new JsonResponseEntity();
        if(flag){
            entity.setMsg("您的糖尿病高危筛查提醒已经发送成功");
        }else{
            entity.setCode(1001);
            entity.setMsg("您的糖尿病高危筛查提醒发送失败");
        }
        return entity;
    }
}

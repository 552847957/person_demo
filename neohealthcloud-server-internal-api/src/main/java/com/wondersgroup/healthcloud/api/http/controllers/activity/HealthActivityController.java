package com.wondersgroup.healthcloud.api.http.controllers.activity;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.repository.activiti.HealthActivityInfoRepository;
import com.wondersgroup.healthcloud.services.user.HealthActivityInfoService;

@RestController
@RequestMapping("/healthActivity")
public class HealthActivityController {

    private Logger logger = Logger.getLogger(HealthActivityController.class);
    
    @Autowired
    private HealthActivityInfoService infoService;
    @Autowired
    HealthActivityInfoRepository   activityRepo;

    @RequestMapping(value = "/listdata")
    public JsonListResponseEntity<HealthActivityInfo> searchActivity(
            @RequestParam(required=false) String onlineTime,
            @RequestParam(required=false) String offlineTime,
            @RequestParam(defaultValue = "1") Integer flag,
            @RequestParam(defaultValue = "10") Integer pageSize
            ){
        JsonListResponseEntity<HealthActivityInfo> entity = new JsonListResponseEntity<HealthActivityInfo>();
        List<HealthActivityInfo> infos = infoService.getHealthActivityInfos(null, null, null, null, flag, pageSize);
        entity.setContent(infos, infos.size() == 10, null, flag.toString());
        return entity;
    }

    @RequestMapping("/findActivity")
    public JsonResponseEntity<HealthActivityInfo> findActivitie(
            @RequestParam() String acitivityId
            ){
        JsonResponseEntity<HealthActivityInfo> entity = new JsonResponseEntity<HealthActivityInfo>();
        HealthActivityInfo info = activityRepo.findOne(acitivityId);
        entity.setData(info);
        return entity;
    }

    @RequestMapping(value = "/saveActivity",method= RequestMethod.POST)
    public JsonResponseEntity<String> saveActivity(@RequestBody String request){
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        HealthActivityInfo info = new Gson().fromJson(request, HealthActivityInfo.class);
        if(StringUtils.isEmpty(info.getActivityid())){
            info.setActivityid(IdGen.uuid());
            info.setDelFlag("0");
            activityRepo.saveAndFlush(info);
        }else{
            activityRepo.saveAndFlush(info);
        }
        return entity;
    }

    /**
     * 根据activityId复制一个新活动
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/copyActivity",method= RequestMethod.GET)
    public JsonResponseEntity<String> copyActivity(@RequestParam String activityId){
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        HealthActivityInfo info = activityRepo.findOne(activityId);
        info.setActivityid(IdGen.uuid());
        info.setTitle(info.getTitle() + "1");
        activityRepo.saveAndFlush(info);
        return entity;
    }

    /**
     * 根据activityId删除一个活动
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/deleteActivity",method= RequestMethod.DELETE)
    public JsonResponseEntity<String> deleteActivity(@RequestParam String activityId){
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        HealthActivityInfo info = activityRepo.findOne(activityId);
        info.setDelFlag("1");
        activityRepo.saveAndFlush(info);
        return entity;
    }

}

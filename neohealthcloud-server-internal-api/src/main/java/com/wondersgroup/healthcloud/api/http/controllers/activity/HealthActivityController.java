package com.wondersgroup.healthcloud.api.http.controllers.activity;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;

@Controller
@RequestMapping("/healthActivity")
public class HealthActivityController {

    private Logger logger = Logger.getLogger(HealthActivityController.class);


    @RequestMapping(value = "/listdata")
    public JsonListResponseEntity<HealthActivityInfo> searchActivity(
            @RequestParam(required=false) String onlineTime,
            @RequestParam(required=false) String offlineTime,
            @RequestParam(defaultValue = "1") String flag,
            @RequestParam(defaultValue = "10") String pageSize
            ){
        
        return null;
    }

    @RequestMapping("/findActivity")
    public JsonResponseEntity<HealthActivityInfo> findActivitie(
            @RequestParam() String acitivityId
            ){
        JsonResponseEntity<HealthActivityInfo> entity = new JsonResponseEntity<HealthActivityInfo>();
        
        
        return entity;
    }

    @RequestMapping(value = "/saveActivity",method= RequestMethod.POST)
    public JsonResponseEntity<String> saveActivity(@RequestBody String request){

//        HealthActivityInfo
        return null;
    }

    /**
     * 根据activityId复制一个新活动
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/copyActivity",method= RequestMethod.GET)
    public JsonResponseEntity<String> copyActivity(@RequestParam String activityId){
        
        
        return null;
    }

    /**
     * 根据activityId删除一个活动
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/deleteActivity",method= RequestMethod.DELETE)
    public JsonResponseEntity<String> deleteActivity(@RequestParam String activityId){
        
        
        return null;
    }

}

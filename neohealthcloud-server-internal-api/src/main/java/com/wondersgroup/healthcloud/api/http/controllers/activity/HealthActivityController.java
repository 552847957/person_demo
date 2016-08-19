package com.wondersgroup.healthcloud.api.http.controllers.activity;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.wondersgroup.healthcloud.api.http.dto.activity.HealthActivityInfoDTO;
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
    public JsonListResponseEntity<HealthActivityInfoDTO> searchActivity(
            @RequestParam(required=false) String onlineTime,
            @RequestParam(required=false) String offlineTime,
            @RequestParam(defaultValue = "1") Integer flag,
            @RequestParam(defaultValue = "10") Integer pageSize
            ){
        JsonListResponseEntity<HealthActivityInfoDTO> entity = new JsonListResponseEntity<HealthActivityInfoDTO>();
        List<HealthActivityInfo> infos = infoService.getHealthActivityInfos(onlineTime, offlineTime, flag, pageSize);
        List<HealthActivityInfoDTO> infoDTOs = HealthActivityInfoDTO.infoDTO(infos);
        entity.setContent(infoDTOs, infoDTOs.size() == 10, null, String.valueOf((flag + 1)));
        entity.setMsg("查询成功");
        return entity;
    }

    @RequestMapping("/findActivity")
    public JsonResponseEntity<HealthActivityInfoDTO> findActivitie(
            @RequestParam() String acitivityId
            ){
        JsonResponseEntity<HealthActivityInfoDTO> entity = new JsonResponseEntity<HealthActivityInfoDTO>();
        HealthActivityInfo info = activityRepo.findOne(acitivityId);
        entity.setData(new HealthActivityInfoDTO(info));
        entity.setMsg("查询成功");
        return entity;
    }
    
    @RequestMapping(value = "/saveActivity",method= RequestMethod.POST)
    public JsonResponseEntity<String> saveActivity(@RequestBody String request){
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        HealthActivityInfo info = new Gson().fromJson(request, HealthActivityInfo.class);
        info.setDelFlag("0");
        info.setStyle(1);
        info.setOnlineStatus("1");//已上线
        if(StringUtils.isEmpty(info.getActivityid())){
            info.setActivityid(IdGen.uuid());
            activityRepo.save(info);
            entity.setMsg("添加成功");
        }else{
            activityRepo.saveAndFlush(info);
            entity.setMsg("修改成功");
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
        HealthActivityInfo saveInfo = new HealthActivityInfo();
        BeanUtils.copyProperties(info, saveInfo);
        saveInfo.setActivityid(IdGen.uuid());
        saveInfo.setTitle(info.getTitle() + "1");
        saveInfo.setUpdateDate(new Date());
        activityRepo.save(saveInfo);
        entity.setMsg("复制成功");
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
        entity.setMsg("删除成功");
        return entity;
    }

}

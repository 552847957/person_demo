package com.wondersgroup.healthcloud.api.http.controllers.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.entity.area.DicArea;
import com.wondersgroup.healthcloud.jpa.repository.activity.HealthActivityInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.area.DicAreaRepository;
import com.wondersgroup.healthcloud.services.user.HealthActivityInfoService;

@RestController
@RequestMapping("/healthActivity")
public class HealthActivityController {

    private Logger                    logger = Logger.getLogger(HealthActivityController.class);

    @Autowired
    private HealthActivityInfoService infoService;
    
    @Autowired
    private HealthActivityInfoRepository      activityRepo;
    
    @Autowired
    private DicAreaRepository dicAreaRepository;
    
    @RequestMapping(value = "/listdata", method = RequestMethod.POST)
    public JsonListResponseEntity<HealthActivityInfoDTO> searchActivity(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String status =  reader.readString("status", true);
        String title =  reader.readString("title", true);
        String onlineTime =  reader.readString("onlineTime", true);
        String offlineTime =  reader.readString("offlineTime", true);
        int flag =  reader.readDefaultInteger("flag", 1);
        int pageSize =  reader.readDefaultInteger("pageSize", 10);
        
        JsonListResponseEntity<HealthActivityInfoDTO> entity = new JsonListResponseEntity<HealthActivityInfoDTO>();
        List<HealthActivityInfo> infos = infoService.getHealthActivityInfos(status, title, onlineTime, offlineTime,
                flag, pageSize);
        int count = infoService.getHealthActivityInfoCount(status, title, onlineTime, offlineTime);
        List<HealthActivityInfoDTO> infoDTOs = HealthActivityInfoDTO.infoDTO(infos);
        entity.setContent(infoDTOs, infoDTOs.size() == 10, null, String.valueOf((flag + 1)));
        Map<String, Object> extras = new HashMap<String, Object>();
        int ps = count / pageSize;
        extras.put("total_pages", count % pageSize == 0 ? ps : ps + 1);
        extras.put("total_elements", count);
        entity.setExtras(extras);
        entity.setMsg("查询成功");
        return entity;
    }

    @RequestMapping("/findActivity")
    public JsonResponseEntity<HealthActivityInfoDTO> findActivitie(@RequestParam() String acitivityId) {
        JsonResponseEntity<HealthActivityInfoDTO> entity = new JsonResponseEntity<HealthActivityInfoDTO>();
        HealthActivityInfo info = activityRepo.findOne(acitivityId);
        entity.setData(new HealthActivityInfoDTO(info));
        entity.setMsg("查询成功");
        return entity;
    }

    @RequestMapping(value = "/saveActivity", method = RequestMethod.POST)
    public JsonResponseEntity<String> saveActivity(@RequestBody String request) {
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        HealthActivityInfo info = new Gson().fromJson(request, HealthActivityInfo.class);
        info.setDelFlag("0");
        info.setStyle(1);
        if (info.getOnlineTime().after(new Date())) {
            info.setOnlineStatus("0");//未上线
        } else if (info.getOfflineTime().before(new Date())) {
            info.setOnlineStatus("2");//已下线
        } else {
            info.setOnlineStatus("1");//已上线
        }
        if (StringUtils.isEmpty(info.getActivityid())) {
            info.setActivityid(IdGen.uuid());
            info.setCreateDate(new Date());
            activityRepo.save(info);
            entity.setMsg("添加成功");
        } else {
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
    @RequestMapping(value = "/copyActivity", method = RequestMethod.GET)
    public JsonResponseEntity<String> copyActivity(@RequestParam String activityId) {
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
    @RequestMapping(value = "/deleteActivity", method = RequestMethod.DELETE)
    public JsonResponseEntity<String> deleteActivity(@RequestParam String activityId) {
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        String[] ids = activityId.split(",");
        for (String id : ids) {
            if(StringUtils.isEmpty(id)){
               continue; 
            }
            HealthActivityInfo info = activityRepo.findOne(id);
            info.setDelFlag("1");
            activityRepo.saveAndFlush(info);
        }
        entity.setMsg("删除成功");
        return entity;
    }
    
    /**
     * 查询省市区字段表数据
     * @param upperCode
     * @return JsonListResponseEntity<DicArea>
     */
    @RequestMapping(value = "/firstAddressInfo", method = RequestMethod.GET)
    public JsonListResponseEntity<DicArea> getFirstAddressInfo(@RequestParam(required = false) String upperCode) {
        JsonListResponseEntity<DicArea> entity = new JsonListResponseEntity<DicArea>();
        if(StringUtils.isEmpty(upperCode)){
            entity.setContent(dicAreaRepository.getAddressListByLevel("1"));
        }else{
            entity.setContent(dicAreaRepository.getAddressListByLevelAndFatherId(upperCode));
        }
        return entity;
    }

}

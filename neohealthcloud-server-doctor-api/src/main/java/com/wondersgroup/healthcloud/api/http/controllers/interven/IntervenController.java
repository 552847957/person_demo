package com.wondersgroup.healthcloud.api.http.controllers.interven;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.interven.InterventionAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.interven.InterventionDetailAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.interven.InterventionExportAPIEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.interven.IntervenService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2015/9/8.
 */
@RestController
@RequestMapping("/api/interven")
public class IntervenController {

    @Autowired
    private IntervenService intervenService;

    /**
     * 获取需要干预的用户信息
     *
     * param personcards 患者身份证信息集合
     * param query 查询条件
     * param type 干预类型 如：10000,20000【逗号间隔】
     * @return
     */
    @RequestMapping(value = "/count", method = RequestMethod.POST)
    @VersionRange
    @ResponseBody
    public JsonResponseEntity<Map<String,String>> getIntervenCount(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String personcards =  reader.readString("personcards", false);

        JsonResponseEntity<Map<String,String>> response = new JsonResponseEntity<Map<String,String>>();
        Integer count = intervenService.findAllIntervenCount(personcards);
        response.setData(ImmutableMap.of("count", null == count ? "0" : count.toString()));
        return response;
    }

    /**
     * 首页获取需要干预的所有用户信息
     *
     * param personcards 患者身份证信息集合
     * @return
     */
    @RequestMapping(value = "/home/list", method = RequestMethod.POST)
    @VersionRange
    @ResponseBody
    public JsonResponseEntity<List<InterventionExportAPIEntity>> getIntervenHomeList(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);

        String personcards =  reader.readString("personcards", false);
        String pageSize = reader.readString("pageSize", true);
        pageSize = (null == pageSize || "".equals(pageSize))?"10":pageSize;

        List<Map<String,Object>> infoList = intervenService.getIntervenHome(personcards,pageSize);
        List<InterventionExportAPIEntity> list = Lists.newArrayList();
        for(Map<String,Object> map:infoList){
            InterventionExportAPIEntity info= new InterventionExportAPIEntity();
            info.setName(null == map.get("name") ? "" : map.get("name").toString());
            info.setExcName(null == map.get("exc_name") ? "" : map.get("exc_name").toString());
            info.setRemindDate(null == map.get("warn_date") ? "" : new SimpleDateFormat("yyyy/MM/dd").format(DateFormatter.parseDateTime(map.get("warn_date").toString())));
            list.add(info);
        }
        JsonResponseEntity<List<InterventionExportAPIEntity>> response = new JsonResponseEntity<List<InterventionExportAPIEntity>>();
        response.setData(list);
        return response;
    }

    /**
     * 获取需要干预的所有用户信息
     *
     * param personcards 患者身份证信息集合
     * param query 查询条件
     * param type 干预类型 如：10000,20000【逗号间隔】
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @VersionRange
    @ResponseBody
    public JsonResponseEntity<List<InterventionExportAPIEntity>> getIntervenList(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);

        String personcards =  reader.readString("personcards", false);
        String query = reader.readString("query", true);
        String type = reader.readString("type", true);

        List<Map<String,Object>> infoList = intervenService.getInterven(personcards, query, type);
        List<InterventionExportAPIEntity> list = Lists.newArrayList();
        for(Map<String,Object> map:infoList){
            InterventionExportAPIEntity info= new InterventionExportAPIEntity();
            info.setName(null == map.get("name") ? "" : map.get("name").toString());
            info.setPersoncardAbbr(null == map.get("personcard") ? "" : map.get("personcard").toString());
            info.setAge(null == map.get("birthday") ? "" : IdcardUtils.getAgeByBirthday(DateFormatter.parseDate(map.get("birthday").toString())) + "岁");
            info.setGender(null == map.get("gender") ? "" : map.get("gender").equals("1") ? "男" : "女");
            info.setMobilephone(null == map.get("regmobilephone") ? "" : map.get("regmobilephone").toString());
            info.setRegisterId(null == map.get("registerid") ? "" : map.get("registerid").toString());
            info.setExcName(null == map.get("exc_name") ? "" : map.get("exc_name").toString());
            info.setRemindDate(null == map.get("warn_date") ? "" : DateFormatter.dateFormat(DateFormatter.parseDateTime(map.get("warn_date").toString())));
            list.add(info);
        }
        JsonResponseEntity<List<InterventionExportAPIEntity>> response = new JsonResponseEntity<List<InterventionExportAPIEntity>>();
        response.setData(list);
        return response;
    }

    /**
     * 获取需要干预的所有用户信息
     *
     * @return
     */
    @RequestMapping(value = "/page/list", method = RequestMethod.POST)
    @VersionRange
    @ResponseBody
    public JsonResponseEntity<InterventionAPIEntity> getIntervenPageList(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);

        String personcards =  reader.readString("personcards", false);
        Integer pageNo =  reader.readInteger("pageNo", false);
        Integer pageSize =  reader.readInteger("pageSize", false);
        String query = reader.readString("query", true);
        String type = reader.readString("type", true);

        JsonResponseEntity<InterventionAPIEntity> response = new JsonResponseEntity<InterventionAPIEntity>();
        Integer total = intervenService.getIntervenCount(personcards, query, type);

        List<InterventionAPIEntity.Info> list = Lists.newArrayList();
        if(total > (pageNo-1) * pageSize){
            List<Map<String,Object>> infoList= intervenService.getInterven(personcards,query,type,pageNo,pageSize);
            for(Map<String,Object> map:infoList){
                String personcard = null == map.get("personcard") ? "" : map.get("personcard").toString();
                InterventionAPIEntity.Info info= new InterventionAPIEntity().new Info();
                info.setAbnormalid(null == map.get("abnormalid") ? "" : map.get("abnormalid").toString());
                info.setName(null == map.get("name") ? "" : map.get("name").toString());
                info.setPersoncard(personcard);
                info.setPersoncardAbbr(StringUtils.isEmpty(personcard)? "" : "*"+(personcard.length()<6?personcard:personcard.substring(personcard.length()-6)));
                info.setExcName(null == map.get("exc_name") ? "" : map.get("exc_name").toString());
                info.setRemindDate(null == map.get("warn_date") ? "" : DateFormatter.dateFormat(DateFormatter.parseDateTime(map.get("warn_date").toString())));
                info.setRegisterId(null == map.get("registerid") ? "" : map.get("registerid").toString());
                info.setAge(null == map.get("birthday") ? "" : IdcardUtils.getAgeByBirthday(DateFormatter.parseDate(map.get("birthday").toString())) + "岁");
                info.setGender(null == map.get("gender") ? "" : map.get("gender").equals("1") ? "男" : "女");
                info.setMobilephone(null == map.get("regmobilephone") ? "" : map.get("regmobilephone").toString());
                list.add(info);
            }
        }
        InterventionAPIEntity entity = new InterventionAPIEntity(total,list);
        response.setData(entity);
        return response;
    }


    /**
     * 获取个人异常指标
     * @param
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @VersionRange
    @ResponseBody
    public JsonResponseEntity<InterventionDetailAPIEntity> getIntervenDetail(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);

        String abnormalids =  reader.readString("abnormalids", false);
        List<Map<String,Object>> infoList = intervenService.findAllInterven(abnormalids);

        List<InterventionDetailAPIEntity.Event> eventList = Lists.newArrayList();
        List<InterventionDetailAPIEntity.EventDetail> eventDetailList = Lists.newArrayList();
        List<InterventionDetailAPIEntity.Detail> detailList = Lists.newArrayList();

        Map<String,Object> preMap = null;
        for(Map<String,Object> map:infoList){

            if(null != preMap && !map.get("abnormalid").equals(preMap.get("abnormalid"))){//不同的事件
                eventDetailList.add(new InterventionDetailAPIEntity().new EventDetail(
                        "事件"+(eventDetailList.size()+1),
                        "干预截止日期："+null == preMap.get("remind_date")?"":DateFormatter.dateFormat(DateFormatter.parseDate(preMap.get("remind_date").toString())),detailList));
                detailList = Lists.newArrayList();
            }
            if(null != preMap && !map.get("type").toString().equals(preMap.get("type").toString())) {//不同的异常类型
                eventList.add(new InterventionDetailAPIEntity().new Event(null==preMap.get("exc_name")?"":preMap.get("exc_name").toString()
                        ,null==preMap.get("memo")?"":preMap.get("memo").toString(),eventDetailList));
                eventDetailList = Lists.newArrayList();
                detailList = Lists.newArrayList();
            }

            detailList.add(new InterventionDetailAPIEntity().new Detail(
                    null==map.get("check_date")?"":DateFormatter.dateFormat(DateFormatter.parseDate(map.get("check_date").toString())),
                    null == map.get("check_result")?"":map.get("check_result").toString()));
            preMap = map;
        }
        if(null != infoList && infoList.size()>0){
            eventDetailList.add(new InterventionDetailAPIEntity().new EventDetail(
                    "事件"+(eventDetailList.size()+1),
                    "干预截止日期："+null == preMap.get("remind_date")?"":DateFormatter.dateFormat(DateFormatter.parseDate(preMap.get("remind_date").toString())),detailList));
            eventList.add(new InterventionDetailAPIEntity().new Event(null==preMap.get("exc_name")?"":preMap.get("exc_name").toString()
                    ,null==preMap.get("memo")?"":preMap.get("memo").toString(),eventDetailList));
        }
        InterventionDetailAPIEntity entity = new InterventionDetailAPIEntity(abnormalids,null == preMap.get("name")?"":preMap.get("name").toString()+"的异常指标",eventList);
        JsonResponseEntity<InterventionDetailAPIEntity> response = new JsonResponseEntity<InterventionDetailAPIEntity>();
        response.setData(entity);
        return response;
    }

    /**
     * 医生干预
     * @return
     */
    @RequestMapping(value = "/diagnose", method = RequestMethod.POST)
    @VersionRange
    @ResponseBody
    public JsonResponseEntity doDiagnose(@RequestBody String request) {

        JsonResponseEntity response = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String abnormalids =  reader.readString("abnormalids", false);
        String doctorid =  reader.readString("doctorid", false);
        String content = reader.readString("content", false);
        int count = intervenService.updateInterven(abnormalids,doctorid,content);
        if(count > 0) {
            response.setMsg("干预成功");
        }else{
            response.setCode(1661);
            response.setMsg("干预失败");
        }

        return response;
    }
}

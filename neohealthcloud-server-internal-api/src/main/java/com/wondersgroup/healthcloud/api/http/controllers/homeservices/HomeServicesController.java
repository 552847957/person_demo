package com.wondersgroup.healthcloud.api.http.controllers.homeservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.api.http.dto.cloudtopline.CloudTopLineViewDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.enums.CloudTopLineEnum;
import com.wondersgroup.healthcloud.jpa.enums.ServiceTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicRepository;
import com.wondersgroup.healthcloud.jpa.repository.cloudtopline.CloudTopLineRepository;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeUserServiceRepository;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.cloudTopLine.CloudTopLineService;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.homeservice.HomeServices;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 后台服务管理
 * Created by xianglinhai on 2017/05/12.
 */
@RestController
@RequestMapping("/api/homeService")
public class HomeServicesController  {

    @Autowired
    private HomeServices homeServicesImpl;

    @Autowired
    private HomeUserServiceRepository homeUserServiceRepository;


    @VersionRange
    @RequestMapping(value = "/manage/allVersions", method = RequestMethod.GET)
    public Object allVersions(){
        List<String> list = homeServicesImpl.findAllVersions();
        List<Map<String,String>> versons = new ArrayList<Map<String,String>>();
         if(!CollectionUtils.isEmpty(list)){
             for(String str:list){
               Map<String,String> map = new HashMap<String,String>();
                 map.put("version",str);
                 versons.add(map);
             }
         }
        return  new JsonResponseEntity(0, "操作成功!",versons);
    }


    @VersionRange
    @RequestMapping(value = "/manage/getServicesByVersion", method = RequestMethod.GET)
    public Object getServicesByVersion(@RequestParam(value = "version", required = true) String version){
        Map paramMap = new HashMap();
        paramMap.put("version",version);
        List<HomeServiceEntity> list = homeServicesImpl.findHomeServiceByCondition(paramMap);

        return  new JsonResponseEntity(0, "操作成功!",list);
    }

    @VersionRange
    @RequestMapping(value = "/manage/getServiceType", method = RequestMethod.GET)
    public Object getServiceType(){
               List<ServiceType> serviceType = new ArrayList<ServiceType>();
                for(ServiceTypeEnum ste:ServiceTypeEnum.values()){
                    serviceType.add(new ServiceType(ste.getType(),ste.getName()));
                }

        return  new JsonResponseEntity(0, "操作成功!",serviceType);
    }

    @VersionRange
    @RequestMapping(value = "/manage/editHomeServices", method = RequestMethod.POST)
    public Object addCloudTopLine(@RequestBody(required = true) String body) {
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(body);

        return  new JsonResponseEntity(0, "操作成功!");
    }

}

@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
class ServiceType{
    public ServiceType (){}
    public ServiceType(int id,String name){this.id = id;this.name = name;}
      private int id;
      private String name;
}
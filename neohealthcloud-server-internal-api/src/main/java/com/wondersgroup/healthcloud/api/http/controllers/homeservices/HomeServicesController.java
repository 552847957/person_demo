package com.wondersgroup.healthcloud.api.http.controllers.homeservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.enums.ServiceTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeUserServiceRepository;
import com.wondersgroup.healthcloud.services.homeservice.HomeServices;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeServiceDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台服务管理
 * Created by xianglinhai on 2017/05/12.
 */
@RestController
@RequestMapping("/api/homeService")
public class HomeServicesController {

    @Autowired
    private HomeServices homeServicesImpl;


    @VersionRange
    @RequestMapping(value = "/manage/allVersions", method = RequestMethod.GET)
    public Object allVersions(@RequestParam(value = "version", required = false) String version) {
        List<String> list = homeServicesImpl.findAllVersions(version);
        List<Map<String, String>> versons = new ArrayList<Map<String, String>>();
        if (!CollectionUtils.isEmpty(list)) {
            for (String str : list) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("version", str);
                versons.add(map);
            }
        }
        return new JsonResponseEntity(0, "操作成功!", versons);
    }


    @VersionRange
    @RequestMapping(value = "/manage/getServicesByVersion", method = RequestMethod.GET)
    public Object getServicesByVersion(@RequestParam(value = "version", required = true) String version) {
        Map paramMap = new HashMap();
        paramMap.put("version", version);
        List<HomeServiceEntity> list = homeServicesImpl.findHomeServiceByCondition(paramMap);
        List<HomeServiceDTO> listDto = new ArrayList<HomeServiceDTO>();
        for (HomeServiceEntity entity : list) {
            listDto.add(new HomeServiceDTO(entity));
        }
        return new JsonResponseEntity(0, "操作成功!", listDto);
    }

    @VersionRange
    @RequestMapping(value = "/manage/getServiceType", method = RequestMethod.GET)
    public Object getServiceType() {
        List<ServiceType> serviceType = new ArrayList<ServiceType>();
        for (ServiceTypeEnum ste : ServiceTypeEnum.values()) {
            serviceType.add(new ServiceType(ste.getType(), ste.getName()));
        }

        return new JsonResponseEntity(0, "操作成功!", serviceType);
    }

    @VersionRange
    @RequestMapping(value = "/manage/editHomeServices", method = RequestMethod.POST)
    public Object editHomeServices(@RequestBody(required = true) String body) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(body).getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();//获取数组的值并转化为JsonObjecty格式
            HomeServiceEntity entity = gson.fromJson(subObject, HomeServiceEntity.class);//重要

              if(null == entity){
                  return new JsonResponseEntity(-1, "內部錯誤，数据解析异常!");
              }

            if(StringUtils.isBlank(entity.getVersion())){
                return new JsonResponseEntity(-1, "版本号不能为空!");
            }

            if(!entity.getVersion().matches("\\d+\\.\\d+\\.\\d+")){
                return new JsonResponseEntity(-1, "版本号不满足规则!");
            }

            if(StringUtils.isNotBlank(entity.getRecommendTitle()) && entity.getRecommendTitle().length() > 3){
                return new JsonResponseEntity(-1, "推荐不能超过三个字!");
            }

            if (StringUtils.isNotBlank(entity.getId())) { //修改
                homeServicesImpl.updateHomeService(entity);
            } else if (StringUtils.isBlank(entity.getId())) {//新增
                homeServicesImpl.saveHomeService(entity);
            }
        }

        return new JsonResponseEntity(0, "操作成功!");
    }
}



@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
class ServiceType {
    public ServiceType() {
    }

    public ServiceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;
}
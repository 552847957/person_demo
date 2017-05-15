package com.wondersgroup.healthcloud.api.http.controllers.homeservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeTabServiceEntity;
import com.wondersgroup.healthcloud.jpa.enums.TabServiceTypeEnum;
import com.wondersgroup.healthcloud.services.homeservice.HomeServices;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeTabServiceDTO;
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
 * 後台APP地步tab服务管理
 * Created by xianglinhai on 2017/05/12.
 */
@RestController
@RequestMapping("/api/homeTabService")
public class HomeTabServiceController {

    @Autowired
    private HomeServices homeServicesImpl;


    @VersionRange
    @RequestMapping(value = "/manage/allVersions", method = RequestMethod.GET)
    public Object allVersions(@RequestParam(value = "version", required = false) String version) {
        List<String> list = homeServicesImpl.findAllHomeTabServiceVersions(version);
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
    @RequestMapping(value = "/manage/getTabServiceByVersion", method = RequestMethod.GET)
    public Object getServicesByVersion(@RequestParam(value = "version", required = true) String version) {
        Map paramMap = new HashMap();
        paramMap.put("version", version);
        List<HomeTabServiceEntity> list = homeServicesImpl.findMyHomeTabService(paramMap);
        List<HomeTabServiceDTO> listDto = new ArrayList<HomeTabServiceDTO>();
        for (HomeTabServiceEntity entity : list) {
            listDto.add(new HomeTabServiceDTO(entity));
        }
        return new JsonResponseEntity(0, "操作成功!", listDto);
    }

    @VersionRange
    @RequestMapping(value = "/manage/getTabServiceType", method = RequestMethod.GET)
    public Object getTabServiceType() {
        List<TabServiceType> serviceType = new ArrayList<>();
        for (TabServiceTypeEnum ste : TabServiceTypeEnum.values()) {
            serviceType.add(new TabServiceType(ste.getType(), ste.getName()));
        }
        return new JsonResponseEntity(0, "操作成功!", serviceType);

    }

    @VersionRange
    @RequestMapping(value = "/manage/editTabHomeService", method = RequestMethod.POST)
    public Object addCloudTopLine(@RequestBody(required = true) String body) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(body).getAsJsonArray();// object.get("student").getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();//获取数组的值并转化为JsonObjecty格式
            HomeTabServiceEntity entity = gson.fromJson(subObject, HomeTabServiceEntity.class);//重要
            if (null != entity && StringUtils.isNotBlank(entity.getId())) { //修改
                homeServicesImpl.updateHomeTabService(entity);
            } else if (null != entity && StringUtils.isBlank(entity.getId())) {//新增
                homeServicesImpl.saveHomeTabService(entity);
            }
        }

        return new JsonResponseEntity(0, "操作成功!");
    }

}

@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
class TabServiceType {
    public TabServiceType() {
    }

    public TabServiceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;
}


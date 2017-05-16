package com.wondersgroup.healthcloud.api.http.controllers.homeservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.*;
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
        List<HomeTabServiceDTO> highlight = new ArrayList<HomeTabServiceDTO>(); //高亮
        List<HomeTabServiceDTO> noHighlight = new ArrayList<HomeTabServiceDTO>(); //非高亮
        List<HomeTabServiceDTO> background = new ArrayList<HomeTabServiceDTO>();//背景
        for (HomeTabServiceEntity entity : list) {
            if(TabServiceTypeEnum.HIGHTLIGHT.getType().equals(entity.getTabType())){
                highlight.add(new HomeTabServiceDTO(entity));
            }
            if(TabServiceTypeEnum.NO_HIGHTLIGHT.getType().equals(entity.getTabType())){
                noHighlight.add(new HomeTabServiceDTO(entity));
            }
            if(TabServiceTypeEnum.BACKGROUND.getType().equals(entity.getTabType())){
                background.add(new HomeTabServiceDTO(entity));
            }
        }
        return new JsonResponseEntity(0, "操作成功!", new TabServiceData(highlight,noHighlight,background));
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

    public JsonResponseEntity checkObject(List<HomeTabServiceEntity> list){

      for(HomeTabServiceEntity entity:list){
        /*if(null == entity){
            return new JsonResponseEntity(-1, "內部錯誤，数据解析异常!");
        }*/

        if(StringUtils.isBlank(entity.getVersion())){
            return new JsonResponseEntity(-1, "版本号不能为空!");
        }

        if(!entity.getVersion().matches("\\d+\\.\\d+\\.\\d+")){
            return new JsonResponseEntity(-1, "版本号不满足规则!");
        }

        if(StringUtils.isBlank(entity.getImgUrl())){
            return new JsonResponseEntity(-1," 图片地址不能为空!");
        }
     }
        return null;
    }

    JsonResponseEntity saveTabService(List<HomeTabServiceEntity> list,TabServiceTypeEnum tb){
        for( HomeTabServiceEntity entity:list){

            entity.setTabType(tb.getType());
            if (StringUtils.isNotBlank(entity.getId())) { //修改
                homeServicesImpl.updateHomeTabService(entity);
            } else if (StringUtils.isBlank(entity.getId())) {//新增
                entity.setDelFlag("0");
                homeServicesImpl.saveHomeTabService(entity);
            }
        }

        return null;
    }

    List<HomeTabServiceEntity> toEntityList(JsonArray array,String version){
        Gson gson = new Gson();
        List<HomeTabServiceEntity> list = new ArrayList<HomeTabServiceEntity>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();//获取数组的值并转化为JsonObjecty格式
            HomeTabServiceEntity entity = gson.fromJson(subObject, HomeTabServiceEntity.class);
            entity.setVersion(version);
            list.add(entity);
        }
        return list;
    }


    @VersionRange
    @RequestMapping(value = "/manage/editTabHomeService", method = RequestMethod.POST)
    public Object editTabHomeService(@RequestBody(required = true) String body) {

        JsonParser parser = new JsonParser();
        JsonObject jo = parser.parse(body).getAsJsonObject();
        JsonElement version = jo.get("version") ;
         if(null == version){
             return new JsonResponseEntity(-1, "版本号不能为空!");
         }

        JsonArray backgroundArray =  jo.getAsJsonArray("background");//背景
        JsonArray noHighlightArray = jo.getAsJsonArray("noHighlight");//非高亮
        JsonArray highlightArray = jo.getAsJsonArray("highlight");//高亮

        List<HomeTabServiceEntity> backgroundList =  toEntityList(backgroundArray,version.getAsString());
        List<HomeTabServiceEntity> noHighlightList = toEntityList(noHighlightArray,version.getAsString());
        List<HomeTabServiceEntity> highlightList = toEntityList(highlightArray,version.getAsString());

        JsonResponseEntity entity = null;
        entity = checkObject(backgroundList);

        if(null != entity){
            return entity;
        }

        entity = checkObject(noHighlightList);
        if(null != entity){
            return entity;
        }

        entity = checkObject(highlightList);
        if(null != entity){
            return entity;
        }

        saveTabService(backgroundList,TabServiceTypeEnum.BACKGROUND);
        saveTabService(noHighlightList,TabServiceTypeEnum.NO_HIGHTLIGHT);
        saveTabService(highlightList,TabServiceTypeEnum.HIGHTLIGHT);

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
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class TabServiceData{
    List<HomeTabServiceDTO> highlight = null; //高亮
    List<HomeTabServiceDTO> noHighlight = null; //非高亮
    List<HomeTabServiceDTO> background = null;//背景
    TabServiceData(){};
    TabServiceData(List<HomeTabServiceDTO> highlight,List<HomeTabServiceDTO> noHighlight,List<HomeTabServiceDTO> background ){
        this.highlight = highlight;
        this.noHighlight = noHighlight;
        this.background = background;

    };
}

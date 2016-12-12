package com.wondersgroup.healthcloud.api.http.controllers.moduleportal;

import com.wondersgroup.healthcloud.api.http.dto.moduleportal.ModulePortalViewDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import com.wondersgroup.healthcloud.jpa.enums.VisibleEnum;
import com.wondersgroup.healthcloud.services.modulePortal.ModulePortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块入口
 * Created by xianglinhai on 2016/12/12.
 */
@RestController
@RequestMapping(path = "/api/modulePortal")
public class ModulePortalController {
    @Autowired
    ModulePortalService modulePortalService;

    @VersionRange
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object getcloudTopLineList(){
        Map<String, Object> paramMap = new HashMap<String,Object>();
        List<ModulePortal> list = modulePortalService.queryAllModulePortal();
        List<ModulePortalViewDTO> dtoList = new ArrayList<ModulePortalViewDTO>();
        if(!CollectionUtils.isEmpty(list)){
            for(ModulePortal entity:list){
                dtoList.add(new ModulePortalViewDTO(entity));
            }

        }

        return new JsonResponseEntity<List<ModulePortalViewDTO>>(0, "获取数据成功",dtoList);
    }


    @VersionRange
    @RequestMapping(value = "/manage/add", method = RequestMethod.POST)
    public Object addModulePortal(@RequestBody(required = false) String body){
        JsonKeyReader reader = new JsonKeyReader(body);
        String itemName = reader.readString("itemName",false);
        String iconUrl = reader.readString("iconUrl",false);
        String mainTitle = reader.readString("mainTitle",false);
        String subTitle = reader.readString("subTitle",false);
        String jumpUrl = reader.readString("jumpUrl",false);
        Integer sort = reader.readInteger("sort",false);

        if(StringUtils.isBlank(itemName)){
            return  new JsonResponseEntity(1, "itemName 为空",null);
        }

        if(StringUtils.isBlank(iconUrl)){
            return  new JsonResponseEntity(1, "iconUrl 为空",null);
        }

        if(StringUtils.isBlank(mainTitle)){
            return  new JsonResponseEntity(1, "mainTitle 为空",null);
        }

        if(StringUtils.isBlank(subTitle)){
            return  new JsonResponseEntity(1, "subTitle 为空",null);
        }


        if(StringUtils.isBlank(jumpUrl)){
            return  new JsonResponseEntity(1, "jumpUrl 为空",null);
        }

        if(null == sort){
            return  new JsonResponseEntity(1, "sort 排序字段不能为空",null);
        }



        ModulePortal modulePortal = new ModulePortal();
        modulePortal.setItemName(itemName);
        modulePortal.setIconUrl(iconUrl);
        modulePortal.setMainTitle(mainTitle);
        modulePortal.setSubTitle(subTitle);
        modulePortal.setJumpUrl(jumpUrl);
        modulePortal.setSort(sort);

        ModulePortal addEntity = modulePortalService.saveModulePortal(modulePortal);
        if(null == addEntity){
            new JsonResponseEntity(1, "数据保存失败",null);
        }

        return  new JsonResponseEntity(0, "数据保存成功",null);
    }


    @VersionRange
    @RequestMapping(value = "/manage/modify", method = RequestMethod.POST)
    public Object modifyModulePortal(@RequestBody(required = false) String body){
        JsonKeyReader reader = new JsonKeyReader(body);
        Integer id = reader.readInteger("id",false);
        String itemName = reader.readString("itemName",false);
        String mainTitle = reader.readString("mainTitle",false);
        String subTitle = reader.readString("subTitle",false);
        String iconUrl = reader.readString("iconUrl",false);
        String jumpUrl = reader.readString("jumpUrl",false);
        String visible = reader.readString("visible",false);
        Integer sort = reader.readInteger("sort",false);

        if(null == id){
            return  new JsonResponseEntity(1, "id 为空",null);
        }else{
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("id",id);
            paramMap.put("del_flag","0");

            List<ModulePortal> list = modulePortalService.queryModulePortalByCondition(paramMap);

            if(CollectionUtils.isEmpty(list)){
                return  new JsonResponseEntity(1, "id 为 "+id+" 记录不存在",null);
            }

        }


        if(StringUtils.isBlank(iconUrl)){
            return  new JsonResponseEntity(1, "iconUrl 为空",null);
        }

        if(StringUtils.isBlank(itemName)){
            return  new JsonResponseEntity(1, "itemName 为空",null);
        }

        if(StringUtils.isBlank(mainTitle)){
            return  new JsonResponseEntity(1, "mainTitle 为空",null);
        }

        if(StringUtils.isBlank(subTitle)){
            return  new JsonResponseEntity(1, "subTitle 为空",null);
        }

        if(StringUtils.isBlank(jumpUrl)){
            return  new JsonResponseEntity(1, "jumpUrl 为空",null);
        }

        if(null == sort){
            return  new JsonResponseEntity(1, "sort 为空",null);
        }else if(sort < 0){
            return  new JsonResponseEntity(1, "sort 为正整数",null);
        }

        if(StringUtils.isBlank(visible)){
            return  new JsonResponseEntity(1, "visible 为空",null);
        }else if(null == VisibleEnum.getEnumById(visible)){
            return  new JsonResponseEntity(1, "visible 值不在范围",null);
        }


        ModulePortal modulePortal = new ModulePortal();
        modulePortal.setId(id);
        modulePortal.setItemName(itemName);
        modulePortal.setIconUrl(iconUrl);
        modulePortal.setMainTitle(mainTitle);
        modulePortal.setSubTitle(subTitle);
        modulePortal.setJumpUrl(jumpUrl);
        modulePortal.setSort(sort);
        modulePortal.setIsVisible(visible);

        boolean flag = modulePortalService.updateModulePortalById(modulePortal);
        if(!flag){
            new JsonResponseEntity(1, "数据修改失败",null);
        }

        return  new JsonResponseEntity(0, "数据修改成功",null);
    }


    @VersionRange
    @RequestMapping(value = "/manage/deleteById", method = RequestMethod.POST)
    public Object deleteModulePortal(@RequestBody(required = false) String body){
        JsonKeyReader reader = new JsonKeyReader(body);
        Integer id = reader.readInteger("id",false);
        if(null == id){
            return  new JsonResponseEntity(1, "id 为空",null);
        }else{
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("id",id);
            paramMap.put("del_flag","0");
            List<ModulePortal> list = modulePortalService.queryModulePortalByCondition(paramMap);
            if(CollectionUtils.isEmpty(list)){
                return  new JsonResponseEntity(1, "id 为 "+id+" 记录不存在",null);
            }else{
                if("1".equals(list.get(0).getDelFlag())){
                    return  new JsonResponseEntity(1, "id 为 "+id+" 记录已经被删除",null);
                }
            }
        }

        boolean flag = modulePortalService.delModulePortalById(id);

        if(!flag){
            new JsonResponseEntity(1, "数据删除失败",null);
        }

        return  new JsonResponseEntity(0, "数据删除成功",null);
    }
}

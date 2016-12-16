package com.wondersgroup.healthcloud.api.http.controllers.cloudtopline;

import com.wondersgroup.healthcloud.api.http.dto.cloudtopline.CloudTopLineViewDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.enums.CloudTopLineEnum;
import com.wondersgroup.healthcloud.services.cloudTopLine.CloudTopLineService;
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
 * 云头条
 * Created by xianglinhai on 2016/12/9.
 */
@RestController
@RequestMapping("/api/cloudTopLine")
public class CloudTopLineController {

    @Autowired
    CloudTopLineService cloudTopLineService;

    @VersionRange
    @RequestMapping(value = "/manage/list", method = RequestMethod.GET)
    public Object getcloudTopLineList(){
        Map<String, Object> paramMap = new HashMap<String,Object>();
         paramMap.put("delFlag","0");
        List<CloudTopLine> list = cloudTopLineService.queryCloudTopLineByCondition(paramMap);
        List<CloudTopLineViewDTO> dtoList = new ArrayList<CloudTopLineViewDTO>();
    if(!CollectionUtils.isEmpty(list)){
      for(CloudTopLine entity:list){
        CloudTopLineViewDTO dto = new CloudTopLineViewDTO(entity);
        dtoList.add(dto);
      }
     }
        return new JsonResponseEntity<List<CloudTopLineViewDTO>>(0, "获取数据成功",dtoList);
    }


    @VersionRange
    @RequestMapping(value = "/manage/add", method = RequestMethod.POST)
    public Object addCloudTopLine(@RequestBody(required = false) String body){
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(body);
        String name = reader.readString("name",false);
        String iconUrl = reader.readString("iconUrl",false);
        String title = reader.readString("title",false);
        String jumpUrl = reader.readString("jumpUrl",false);
        Integer type = reader.readInteger("type",false);

        if(StringUtils.isBlank(name)){
            name = "云头条";
        }

        if(StringUtils.isBlank(iconUrl)){
            return  new JsonResponseEntity(1, "iconUrl 为空",null);
        }

        if(StringUtils.isBlank(title)){
            return  new JsonResponseEntity(1, "title 为空",null);
        }

        if(StringUtils.isBlank(jumpUrl)){
            return  new JsonResponseEntity(1, "jumpUrl 为空",null);
        }
        if(null == type){
            return  new JsonResponseEntity(1, "type 为空",null);
        }else if(null == CloudTopLineEnum.getNameById(type)){
            return  new JsonResponseEntity(1, "type 分类错误!",null);
        }


        CloudTopLine cloudTopLine = new CloudTopLine();
        cloudTopLine.setName(name);
        cloudTopLine.setIconUrl(iconUrl);
        cloudTopLine.setTitle(title);
        cloudTopLine.setJumpUrl(jumpUrl);
        cloudTopLine.setType(type);

        CloudTopLine addEntity = cloudTopLineService.saveCloudTopLine(cloudTopLine);
         if(null == addEntity){
             new JsonResponseEntity(1, "数据保存失败",null);
         }

        return  new JsonResponseEntity(0, "数据保存成功",null);
    }


    @VersionRange
    @RequestMapping(value = "/manage/modify", method = RequestMethod.POST)
    public Object modifyCloudTopLine(@RequestBody(required = false) String body){
        JsonKeyReader reader = new JsonKeyReader(body);
        Integer id = reader.readInteger("id",false);
        String name = reader.readString("name",false);
        String iconUrl = reader.readString("iconUrl",false);
        String title = reader.readString("title",false);
        String jumpUrl = reader.readString("jumpUrl",false);
        Integer type = reader.readInteger("type",false);
         if(null == id){
             return  new JsonResponseEntity(1, "id 为空",null);
         }else{
             Map<String, Object> paramMap = new HashMap<String, Object>();
             paramMap.put("id",id);
             paramMap.put("del_flag","0");
             List<CloudTopLine> list = cloudTopLineService.queryCloudTopLineByCondition(paramMap);
             if(CollectionUtils.isEmpty(list)){
                 return  new JsonResponseEntity(1, "id 为 "+id+" 记录不存在",null);
             }

         }


        if(StringUtils.isBlank(iconUrl)){
            return  new JsonResponseEntity(1, "iconUrl 为空",null);
        }

        if(StringUtils.isBlank(title)){
            return  new JsonResponseEntity(1, "title 为空",null);
        }

        if(StringUtils.isBlank(jumpUrl)){
            return  new JsonResponseEntity(1, "jumpUrl 为空",null);
        }
        if(null == type){
            return  new JsonResponseEntity(1, "type 为空",null);

        }else if(null == CloudTopLineEnum.getNameById(type)){
            return  new JsonResponseEntity(1, "type 分类错误!",null);
        }


        CloudTopLine cloudTopLine = new CloudTopLine();
        cloudTopLine.setId(id);
        cloudTopLine.setIconUrl(iconUrl);
        cloudTopLine.setTitle(title);
        cloudTopLine.setJumpUrl(jumpUrl);
        cloudTopLine.setType(type);

        boolean flag = cloudTopLineService.updateCloudTopLineById(cloudTopLine);
        if(!flag){
            new JsonResponseEntity(1, "数据修改失败",null);
        }

        return  new JsonResponseEntity(0, "数据修改成功",null);
    }


    @VersionRange
    @RequestMapping(value = "/manage/deleteById", method = RequestMethod.POST)
    public Object deleteCloudTopLine(@RequestBody(required = false) String body){
        JsonKeyReader reader = new JsonKeyReader(body);
        Integer id = reader.readInteger("id",false);
        if(null == id){
            return  new JsonResponseEntity(1, "id 为空",null);
        }else{
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("id",id);
            paramMap.put("del_flag","0");
            List<CloudTopLine> list = cloudTopLineService.queryCloudTopLineByCondition(paramMap);
            if(CollectionUtils.isEmpty(list)){
                return  new JsonResponseEntity(1, "id 为 "+id+" 记录不存在",null);
            }else{
                if("1".equals(list.get(0).getDelFlag())){
                    return  new JsonResponseEntity(1, "id 为 "+id+" 记录已经被删除",null);
                }
            }

        }

        boolean flag = cloudTopLineService.delCloudTopLineById(id);

        if(!flag){
            new JsonResponseEntity(1, "数据删除失败",null);
        }

        return  new JsonResponseEntity(0, "数据删除成功",null);
    }
}
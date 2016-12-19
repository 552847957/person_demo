package com.wondersgroup.healthcloud.api.http.controllers.cloudtopline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondersgroup.healthcloud.api.http.dto.cloudtopline.CloudTopLineViewDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.enums.CloudTopLineEnum;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicRepository;
import com.wondersgroup.healthcloud.jpa.repository.cloudtopline.CloudTopLineRepository;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.cloudTopLine.CloudTopLineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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
    private CloudTopLineService cloudTopLineService;


    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CloudTopLineRepository cloudTopLineRepository;

    @Autowired
    ManageNewsArticleService manageNewsArticleServiceImpl;


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
        String jumpUrl = reader.readString("jumpUrl",true);
        String jumpId = reader.readString("jumpId",true);
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

        if(null == type){
            return  new JsonResponseEntity(1, "type 为空",null);
        }else if(null == CloudTopLineEnum.getNameById(type)){
            return  new JsonResponseEntity(1, "type 分类错误!",null);
        }

        if( CloudTopLineEnum.getNameById(type) == CloudTopLineEnum.TIE_ZI || CloudTopLineEnum.getNameById(type) == CloudTopLineEnum.WEN_ZHANG ){
            if(null == jumpId){
                return  new JsonResponseEntity(1, "jumpId 为空",null);
            }
        }else{//h5时 jumpUrl不能为空
            if(StringUtils.isBlank(jumpUrl)){
                return  new JsonResponseEntity(1, "jumpUrl 为空",null);
            }

        }


        CloudTopLine cloudTopLine = new CloudTopLine();
        cloudTopLine.setName(name);
        cloudTopLine.setIconUrl(iconUrl);
        cloudTopLine.setTitle(title);
        cloudTopLine.setJumpUrl(jumpUrl);
        cloudTopLine.setType(type);
        cloudTopLine.setJumpId(jumpId);

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
        String jumpUrl = reader.readString("jumpUrl",true);
        String jumpId = reader.readString("jumpId",true);
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

        if(null == type){
            return  new JsonResponseEntity(1, "type 为空",null);

        }else if(null == CloudTopLineEnum.getNameById(type)){
            return  new JsonResponseEntity(1, "type 分类错误!",null);
        }

        if( CloudTopLineEnum.getNameById(type) == CloudTopLineEnum.TIE_ZI || CloudTopLineEnum.getNameById(type) == CloudTopLineEnum.WEN_ZHANG ){
            if(null == jumpId){
                return  new JsonResponseEntity(1, "jumpId 为空",null);
            }
        }else{//h5时 jumpUrl不能为空
            if(StringUtils.isBlank(jumpUrl)){
                return  new JsonResponseEntity(1, "jumpUrl 为空",null);
            }

        }


        CloudTopLine cloudTopLine = new CloudTopLine();
        cloudTopLine.setId(id);
        cloudTopLine.setIconUrl(iconUrl);
        cloudTopLine.setTitle(title);
        cloudTopLine.setJumpUrl(jumpUrl);
        cloudTopLine.setJumpId(jumpId);
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

    @VersionRange
    @RequestMapping(value = "/manage/getTitle", method = RequestMethod.GET)
    public Object getTitleByTypeAndId(@RequestParam(value = "id", required = true) Integer id,
                                      @RequestParam(value = "type", required = true) Integer type){

        JsonResponseEntity result = new JsonResponseEntity();
        CloudTopLineEnum cloudTopeLineEnum = CloudTopLineEnum.getNameById(type);
        if(null == cloudTopeLineEnum){
            result.setCode(1000);
            result.setMsg("type 类型不对！");
            return result;
        }

        Map<String,String> map = new HashMap<String,String>();

        if(CloudTopLineEnum.TIE_ZI == CloudTopLineEnum.getNameById(type)){  //查询帖子
            Topic topic = topicRepository.findOne(id);
            if(null != topic){
                map.put("title",topic.getTitle());
            }
        }

        if(CloudTopLineEnum.WEN_ZHANG == CloudTopLineEnum.getNameById(type)){// 查询文章
            NewsArticle newsArticle = manageNewsArticleServiceImpl.findArticleInfoById(id,null);
            if(null != newsArticle){
                map.put("title",newsArticle.getTitle());
            }
        }

        if (null != map && map.size() > 0) {
            result.setCode(0);
            result.setData(map);
            result.setMsg("获取数据成功");
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关数据！");
        }

        return result;
    }

    @VersionRange
    @RequestMapping(value = "/manage/getById", method = RequestMethod.GET)
    public Object getCloudTopLineById(@RequestParam(value = "id", required = true) Integer id){
        JsonResponseEntity result = new JsonResponseEntity();

        CloudTopLine model = cloudTopLineRepository.findOne(id);

        if(null != model){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("id",model.getId());
            map.put("name",model.getName());
            map.put("iconUrl",model.getIconUrl());
            map.put("title",model.getTitle());
            map.put("jumpUrl",model.getJumpUrl());
            map.put("jumpId",model.getJumpId());
            map.put("type",model.getType());

            result.setCode(0);
            result.setData(map);
            result.setMsg("获取数据成功");
        }else{
            result.setCode(1000);
            result.setMsg("未查询到相关数据！");
        }

        return result;
    }

}
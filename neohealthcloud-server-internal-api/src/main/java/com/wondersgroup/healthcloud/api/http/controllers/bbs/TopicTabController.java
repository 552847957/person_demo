package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicTabRepository;
import com.wondersgroup.healthcloud.services.bbs.TopicTabService;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicTabSearchCriteria;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by ys on 16/8/1.
 * 圈子话题标签
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/topicTab")
public class TopicTabController {

    @Autowired
    private TopicTabRepository topicTabRepository;

    @Autowired
    private TopicTabService topicTabService;

    @Autowired
    private CircleRepository circleRepository;

    /**
     * 获取全部圈子
     */
    @Admin
    @RequestMapping(value="/groupAll",method = RequestMethod.GET)
    public JsonResponseEntity groupAll(@RequestParam(required = false, defaultValue = "0") Integer getVaild){
        JsonResponseEntity entity = new JsonResponseEntity();
        List<Circle> circles;
        if (getVaild == 1){
            circles = circleRepository.findAllVaild();
        }else {
            circles = circleRepository.findAll();
        }
        List<Map<String, Object>> rt = new ArrayList<>();
        for (Circle circle : circles){
            Map<String, Object> info = new HashMap<>();
            info.put("circleId", circle.getId());
            info.put("circleName", circle.getName());
            List<TopicTab> topicTabs = topicTabRepository.getTopicTabsByCircleId(circle.getId());
            List<Map<String, Object>> tabList = new ArrayList<>();
            if (topicTabs != null){
                for (TopicTab topicTab : topicTabs){
                    Map<String, Object> infoTab = new HashMap<>();
                    infoTab.put("id",topicTab.getId());
                    infoTab.put("tabName", topicTab.getTabName());
                    tabList.add(infoTab);
                }
            }
            info.put("topicTabs", tabList);
            rt.add(info);
        }
        entity.setData(rt);
        return entity;
    }

    @Admin
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Pager list(@RequestBody Pager pager){
        Map<String, Object> parms = pager.getParameter();
        TopicTabSearchCriteria searchCriteria = new TopicTabSearchCriteria(parms);
        searchCriteria.setPage(pager.getNumber());
        searchCriteria.setPageSize(pager.getSize());
        int totalSize = topicTabService.countTopicTabByCriteria(searchCriteria);
        List<Map<String, Object>> list= topicTabService.getTopicTabListByCriteria(searchCriteria);
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }

    /**
     * 绑定push tag
     */
    @Admin
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public JsonResponseEntity<Object> info(@RequestParam Integer id){
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        TopicTab topicTab = topicTabRepository.findOne(id);
        responseEntity.setData(topicTab);
        return responseEntity;
    }

    /**
     * 绑定push tag
     */
    @Admin
    @RequestMapping(value = "/addTab", method = RequestMethod.POST)
    public JsonResponseEntity<Boolean> addTab(@RequestBody TopicTab topicTab){
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        if (topicTab.getCircleId() == null || topicTab.getCircleId() == 0){
            throw new CommonException("圈子无效");
        }
        if (StringUtils.isEmpty(topicTab.getTabName())){
            throw new CommonException("tab name无效");
        }
        TopicTab topicTabExit = topicTabRepository.getTopicTabsByCircleIdAndName(topicTab.getCircleId(), topicTab.getTabName());
        if (topicTabExit != null){
            if ((null != topicTab.getId() && topicTabExit.getId().intValue() != topicTab.getId()) || null == topicTab.getId()){
                throw new CommonException("标签名不能重复");
            }
        }
        topicTab.setDelFlag(topicTab.getDelFlag().equals("1") ? "1" : "0");
        topicTab.setCreateTime(new Date());
        topicTabRepository.save(topicTab);

        if (topicTab.getId() > 0){
            responseEntity.setMsg("保存成功");
        }else {
            responseEntity.setCode(2001);
            responseEntity.setMsg("保存失败");
        }
        return responseEntity;
    }

}

package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleRepository;
import com.wondersgroup.healthcloud.services.bbs.CircleCategoryService;
import com.wondersgroup.healthcloud.services.bbs.CircleService;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.AdminCircleDto;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleCategoryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by ys on 16/8/18.
 * 话题圈子相关
 *
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/circle")
public class CircleController {

    private static final Logger logger = LoggerFactory.getLogger("CircleController");
    @Autowired
    private CircleRepository circleRepository;
    @Autowired
    private CircleCategoryRepository circleCategoryRepository;
    @Autowired
    protected CircleCategoryService circleCategoryService;
    @Autowired
    private CircleService circleService;

    /**
     * 获取全部圈子分类
     */
    @Admin
    @RequestMapping(value = "/allCate", method = RequestMethod.GET)
    public JsonResponseEntity allCate(@RequestParam(required = false, defaultValue = "0") Integer getVaild) {
        JsonResponseEntity entity = new JsonResponseEntity();
        List<CircleCategory> circleCategories = circleCategoryRepository.findAll();
        entity.setData(circleCategories);
        return entity;
    }

    /**
     * 获取全部圈子
     */
    @Admin
    @RequestMapping(value = "/allCircle", method = RequestMethod.GET)
    public JsonResponseEntity allCircle(@RequestParam(required = false, defaultValue = "0") Integer getVaild) {
        JsonResponseEntity entity = new JsonResponseEntity();
        List<Circle> circles = circleRepository.findAll();
        entity.setData(circles);
        return entity;
    }

    /**
     * 保存、修改圈子分类
     *
     * @param circleCategory
     * @return
     */
    @Admin
    @RequestMapping(value = "/saveCircleCategory", method = RequestMethod.POST)
    public JsonResponseEntity saveCircleCategory(@RequestBody CircleCategory circleCategory) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        try {
            String name = circleCategory.getName();
            if (circleCategory.getId() == null) {// 新增
                if (circleCategoryService.ifCategoryNameExist(name, null)) {
                    jsonResponseEntity.setCode(1022);
                    jsonResponseEntity.setMsg(String.format("分类名称[%s]已存在", name));
                    return jsonResponseEntity;
                }
                if (circleCategory.getCreateTime() == null) {
                    circleCategory.setCreateTime(new Date());
                }
                circleCategoryService.saveCircleCategory(circleCategory);
                jsonResponseEntity.setMsg("保存成功");
                return jsonResponseEntity;
            } else {// 更新，带有id
                Integer id = circleCategory.getId();
                CircleCategory byId = circleCategoryRepository.findOne(id);
                // 检查名字是否重复
                if (circleCategoryService.ifCategoryNameExist(name, id)) {
                    jsonResponseEntity.setCode(1022);
                    jsonResponseEntity.setMsg(String.format("分类名称[%s]已存在", name));
                    return jsonResponseEntity;
                }
                // 存在指定id的数据
                if (byId != null) {
                    byId.setDelFlag(circleCategory.getDelFlag());
                    byId.setName(circleCategory.getName());
                    byId.setRank(circleCategory.getRank());
                    circleCategoryService.saveCircleCategory(byId);
                    jsonResponseEntity.setMsg("保存成功");
                    return jsonResponseEntity;
                }
            }

        } catch (Exception e) {
            String errorMsg = "保存/修改圈子分类出错";
            logger.error(errorMsg, e);
            jsonResponseEntity.setCode(1000);
            jsonResponseEntity.setMsg(errorMsg);
        }
        return jsonResponseEntity;
    }

    /**
     * 保存圈子
     */
    @Admin
    @RequestMapping(value = "/saveCircle", method = RequestMethod.POST)
    public JsonResponseEntity saveCircle(@RequestBody String body) {
        JsonResponseEntity response = new JsonResponseEntity();
        JsonKeyReader jsonKeyReader = new JsonKeyReader(body);
        Integer id = jsonKeyReader.readInteger("id", true);
        String name = jsonKeyReader.readString("name", false);
        // 是否推荐 0:不推荐 1:推荐
        Integer isRecommend = jsonKeyReader.readDefaultInteger("isRecommend", 0);
        // 是否默认关注 0：不关注 1：关注
        Integer isDefaultAttent = jsonKeyReader.readDefaultInteger("isDefaultAttent", 0);
        Integer cateId = jsonKeyReader.readInteger("cateId", false);
        Integer fakeAttentionCount = jsonKeyReader.readInteger("fakeAttentionCount", false);
        if (fakeAttentionCount < 0) {
            response.setCode(1031);
            response.setMsg("起始值应该为正整数");
            return response;
        }
        String description = jsonKeyReader.readString("description", false);
        String icon = jsonKeyReader.readString("icon", false);
        Integer rank = jsonKeyReader.readDefaultInteger("rank", 0);
        String delFlag = jsonKeyReader.readDefaultString("delFlag", "1");

        Circle newData = new Circle();
        newData.setId(id);
        newData.setName(name); // *
        newData.setDescription(description);//*
        newData.setIcon(icon); //*
        newData.setCateId(cateId); //*
        newData.setIsRecommend(isRecommend); //*
        newData.setIsDefaultAttent(isDefaultAttent);//*
        newData.setFakeAttentionCount(fakeAttentionCount);//*
        newData.setRank(rank);
        newData.setDelFlag(delFlag);

        int _id = newData.getId() == null ? -1 : newData.getId();
        int isExists = circleService.checkCircleNameByName(_id, newData.getName());
        if (isExists >= 1) {
            response.setCode(1000);
            response.setMsg("圈子重名,请重新命名!");
            return response;
        }
        boolean isSuccess = circleService.saveOrUpdateCircle(newData);
        if (isSuccess) {
            response.setMsg("OK");
            return response;
        } else {
            response.setCode(1000);
            response.setMsg("操作失败");
            return response;
        }
    }

    /**
     * 圈子分类搜索
     *
     * @param pager
     * @return
     */
    @Admin
    @RequestMapping(value = "/categorySearch", method = RequestMethod.POST)
    public Pager categorySearch(@RequestBody Pager pager) {
        String name = (String) pager.getParameter().get("name");
        String delFlag = (String) pager.getParameter().get("delFlag");

        try {
            List<CircleCategoryDto> dtoList = circleCategoryService.searchCategory(name, delFlag, pager.getNumber(), pager.getSize());
            int count = circleCategoryService.searchCategoryCount(name, delFlag);
            pager.setTotalElements(count);
            pager.setData(dtoList);
        } catch (Exception e) {
            String errorMsg = "查询圈子分类出错";
            logger.error(errorMsg, e);
        }
        return pager;
    }

    /**
     * 圈子分类下拉框数据
     *
     * @return
     */
    @Admin
    @RequestMapping(value = "/comboList", method = RequestMethod.GET)
    public JsonResponseEntity categoryComboList() {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        List<CircleCategoryDto> dtoList = circleCategoryService.getCategoryComboList();
        jsonResponseEntity.setData(dtoList);
        return jsonResponseEntity;
    }

    /**
     * 圈子搜索
     *
     * @param pager
     * @return
     */
    @Admin
    @RequestMapping(value = "/circleSearch", method = RequestMethod.POST)
    public Pager circleSearch(@RequestBody Pager pager) {
        String name = (String) pager.getParameter().get("name");
        Integer cateId = (Integer) pager.getParameter().get("cateId");
        Integer isRecommend = (Integer) pager.getParameter().get("isRecommend");
        Integer isDefaultAttent = (Integer) pager.getParameter().get("isDefaultAttent");
        String delFlag = (String) pager.getParameter().get("delFlag");

        try {
            List<AdminCircleDto> dtoList = circleService.searchCircle(name, cateId, isRecommend, isDefaultAttent, delFlag, pager.getNumber(), pager.getSize());
            int count = circleService.countSearchCircle(name, cateId, isRecommend, isDefaultAttent, delFlag);
            pager.setTotalElements(count);
            pager.setData(dtoList);
        } catch (Exception e) {
            logger.error("查询圈子出错", e);
        }
        return pager;
    }

    @Admin
    @RequestMapping(value = "/getCircleCategoryById", method = RequestMethod.GET)
    public JsonResponseEntity getCircleCategoryById(@RequestParam int id) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        CircleCategory circleCategory = circleCategoryService.findOne(id);
        if (circleCategory != null) {
            jsonResponseEntity.setData(circleCategory);
            return jsonResponseEntity;
        } else {
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg(String.format("未查询到编号[%s]的圈子分类数据", id));
        }
        return jsonResponseEntity;
    }

    @Admin
    @RequestMapping(value = "/getCircleById", method = RequestMethod.GET)
    public JsonResponseEntity getCircleById(@RequestParam int id) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        Circle circle = circleRepository.findOne(id);
        if (circle != null) {
            jsonResponseEntity.setData(circle);
            return jsonResponseEntity;
        } else {
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg(String.format("未查询到编号[%s]的圈子数据", id));
        }
        return jsonResponseEntity;
    }
}

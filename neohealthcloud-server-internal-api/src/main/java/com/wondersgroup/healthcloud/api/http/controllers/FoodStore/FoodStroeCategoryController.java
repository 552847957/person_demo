package com.wondersgroup.healthcloud.api.http.controllers.FoodStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import com.wondersgroup.healthcloud.services.foodStore.FoodStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenbin on 16/8/16.
 */
@RestController
@RequestMapping("api")
public class FoodStroeCategoryController {

    @Autowired
    private FoodStoreService foodStoreService;

    /**
     * 查询食物库分类列表
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "foodStoreCategory/list", method = RequestMethod.GET)
    public String findFoodStoreCategoryList(Integer isShow, @PageableDefault(size = 20) Pageable pageable) throws JsonProcessingException {
        Page<FoodStoreCategory> foodStoreCategoryList;
        if (isShow == null) {
            foodStoreCategoryList = foodStoreService.findAllList(pageable);
        } else {
            foodStoreCategoryList = foodStoreService.findAllListByIsShow(isShow, pageable);
        }
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(FoodStoreCategory.class, new String[]{"tags", "create_time"});
        filterMap.put(PageImpl.class, new String[]{"number_of_elements", "sort", "first"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.serializeAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", foodStoreCategoryList);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询食物库分类详情
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "foodStore/{id}", method = RequestMethod.GET)
    public String findFoodStoreCategory(@PathVariable int id) throws JsonProcessingException {
        FoodStoreCategory foodStoreCategory = foodStoreService.findById(id);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(FoodStoreCategory.class, new String[]{"update_time", "create_time"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.serializeAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", foodStoreCategory);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 新增食物库分类
     * @param para
     * @return
     */
    @RequestMapping(value = "saveFoodStore", method = RequestMethod.POST)
    public JsonResponseEntity saveFoodStoreCategory(@RequestBody Map para){
        FoodStoreCategory foodStoreCategory = MapToBeanUtil.fromMapToBean(FoodStoreCategory.class, para);
        foodStoreCategory.setCreateTime(new Date());
        foodStoreCategory.setUpdateTime(new Date());
        foodStoreService.saveFoodStoreCategory(foodStoreCategory);

        return new JsonResponseEntity(0, "保存成功");
    }

    /**
     * 修改食物库分类
     * @param para
     * @return
     */
    @RequestMapping(value = "updateFoodStore", method = RequestMethod.POST)
    public JsonResponseEntity updateFoodStoreCategory(@RequestBody Map para){
        FoodStoreCategory foodStoreCategory = MapToBeanUtil.fromMapToBean(FoodStoreCategory.class, para);
        foodStoreCategory.setUpdateTime(new Date());
        foodStoreService.saveFoodStoreCategory(foodStoreCategory);

        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 查询状态
     * @return
     */
    @RequestMapping(value = "findIsShow/category", method = RequestMethod.GET)
    public Object findIsShow(){
        Map<String, Object> map = new HashMap<>();
        map.put("is_show", foodStoreService.findIsShow());
        return map;
    }

}
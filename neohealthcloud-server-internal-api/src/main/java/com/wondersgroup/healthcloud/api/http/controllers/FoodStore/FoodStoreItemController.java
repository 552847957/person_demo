package com.wondersgroup.healthcloud.api.http.controllers.FoodStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import com.wondersgroup.healthcloud.services.foodStore.FoodStoreItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenbin on 16/8/17.
 */
@RestController
@RequestMapping("api")
public class FoodStoreItemController {

    @Autowired
    private FoodStoreItemService foodStoreItemService;

    /**
     * 查询食物库列表
     * @param item
     * @param pageable
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "foodStoreItem/list", method = RequestMethod.POST)
    public String findFoodStoreItemList(@RequestBody FoodStoreItem item,
                                        @PageableDefault(size = 20) Pageable pageable) throws JsonProcessingException {
        Page<FoodStoreItem> foodStoreItems = foodStoreItemService.findByExample(item, pageable);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(FoodStoreItem.class,
                new String[]{"id", "category", "rank", "food_name", "icon", "use_suggest", "heat", "is_show", "update_time"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", foodStoreItems);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询食物详情
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "foodStoreItem/{id}", method = RequestMethod.GET)
    public String findFoodStoreItem(@PathVariable int id) throws JsonProcessingException {
        FoodStoreItem foodStoreItem = foodStoreItemService.findById(id);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(FoodStoreItem.class, new String[]{"update_time", "create_time"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.serializeAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", foodStoreItem);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 新增食物
     * @param para
     * @return
     */
    @RequestMapping(value = "saveFoodStoreItem", method = RequestMethod.POST)
    public JsonResponseEntity saveFoodStoreItem(@RequestBody Map para){
        FoodStoreItem foodStoreItem = MapToBeanUtil.fromMapToBean(FoodStoreItem.class, para);
        foodStoreItem.setCreateTime(new Date());
        foodStoreItem.setUpdateTime(new Date());
        foodStoreItemService.saveFoodStoreItem(foodStoreItem);

        return new JsonResponseEntity(0, "新增成功");
    }

    /**
     * 修改食物
     * @param para
     * @return
     */
    @RequestMapping(value = "updateFoodStoreItem", method = RequestMethod.POST)
    public JsonResponseEntity updateFoodStoreItem(@RequestBody Map para){
        FoodStoreItem foodStoreItem = MapToBeanUtil.fromMapToBean(FoodStoreItem.class, para);
        foodStoreItem.setUpdateTime(new Date());
        foodStoreItem.setCreateTime(new Date());
        foodStoreItemService.saveFoodStoreItem(foodStoreItem);

        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 查询状态
     * @return
     */
    @RequestMapping(value = "findIsShow/item", method = RequestMethod.GET)
    public Object findIsShow(){
        Map<String, Object> map = new HashMap<>();
        map.put("is_show", foodStoreItemService.findIsShow());
        return map;
    }

    /**
     * 查询分类
     * @return
     */
    @RequestMapping(value = "findCategoryName", method = RequestMethod.GET)
    public Object findCategoryName(){
        Map<String, Object> map = new HashMap<>();
        map.put("category_name", foodStoreItemService.findCategoryName());
        return map;
    }

    /**
     * 批量更新显示/不显示
     * @param isShow
     * @param itemId
     * @return
     */
    @RequestMapping(value = "updateIsShow", method = RequestMethod.POST)
    public JsonResponseEntity updateIsShow(@RequestParam int isShow,
                                           @RequestParam List<Integer> itemId){
        foodStoreItemService.updateIsShowByIds(isShow, new Date(), itemId);

        return new JsonResponseEntity(0, "修改成功");
    }

}

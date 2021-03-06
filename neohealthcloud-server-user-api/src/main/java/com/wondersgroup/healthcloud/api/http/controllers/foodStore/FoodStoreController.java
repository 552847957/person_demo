package com.wondersgroup.healthcloud.api.http.controllers.foodStore;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.wondersgroup.healthcloud.api.http.dto.foodStore.FoodStoreCategoryListAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.foodStore.FoodStoreItemListAPIEntity;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.foodStore.FoodStoreService;
import com.wondersgroup.healthcloud.utils.EmojiUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/api/foodstore")
public class FoodStoreController {

    @Autowired
    private FoodStoreService manageFoodStoreService;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;


    public static String keyWord = "com.hot.search.foodStore";

    /**
     * 食物库分类
     */
    @WithoutToken
    @VersionRange
    @GetMapping(path = "/catlist")
    public JsonListResponseEntity<FoodStoreCategoryListAPIEntity> catList() {
        JsonListResponseEntity<FoodStoreCategoryListAPIEntity> result = new JsonListResponseEntity<>();
        List<FoodStoreCategoryListAPIEntity> categoryListAPIEntities = new ArrayList<>();

        List<FoodStoreCategory> foodStoreCategoryList = manageFoodStoreService.findCategoryList();
        if (foodStoreCategoryList != null) {
            for (FoodStoreCategory foodStoreCategory : foodStoreCategoryList) {
                categoryListAPIEntities.add(new FoodStoreCategoryListAPIEntity(foodStoreCategory));
            }
        }
        result.setContent(categoryListAPIEntities);
        return result;
    }

    /**
     * 食物库
     */
    @WithoutToken
    @VersionRange
    @GetMapping(path = "/foodlist")
    public JsonListResponseEntity<FoodStoreItemListAPIEntity> foodList(
            @RequestParam(required = false, defaultValue = "") String cate_id,
            @RequestParam(required = false, defaultValue = "") String kw,
            @RequestParam(required = false, defaultValue = "1") String flag) {

        JsonListResponseEntity<FoodStoreItemListAPIEntity> response = new JsonListResponseEntity<>();


        int page = 1;
        int pageSize = 10;
        if (StringUtils.isNotEmpty(flag)){
            page = Integer.valueOf(flag);
        }

        if(StringUtils.isNotBlank(kw)){
            String cleanName = EmojiUtils.cleanEmoji(kw);
            if(kw.length() > cleanName.length()){
                return response;
            }
            return this.getFoodListByKwFromDb(kw, page, pageSize);
        }else if(StringUtils.isNotBlank(cate_id)){
            Integer cateId = Integer.valueOf(cate_id);
            return this.getFoodListByCateId(cateId, page, pageSize);
        }

        return response;
    }

    /**
     * 分类搜索
     * @param cateId
     * @param page
     * @param pageSize
     * @return
     */
    private JsonListResponseEntity<FoodStoreItemListAPIEntity> getFoodListByCateId(Integer cateId, int page, int pageSize){
        JsonListResponseEntity<FoodStoreItemListAPIEntity> result = new JsonListResponseEntity<>();
        List<FoodStoreItemListAPIEntity> foodStoreItemListAPIEntities = new ArrayList<>();
        List<FoodStoreItem> foodStoreItemList = manageFoodStoreService.findFoodStoreItemBaseListByCateId(cateId, page, pageSize);
        Boolean hasMore = false;
        if (null != foodStoreItemList && foodStoreItemList.size() == pageSize){
            List<FoodStoreItem> foodStoreItemList2 = manageFoodStoreService.findFoodStoreItemBaseListByCateId(cateId, page+1, 1);
            if (null != foodStoreItemList2){
                hasMore = true;
            }
        }
        if (foodStoreItemList != null) {
            for (FoodStoreItem foodStoreItem : foodStoreItemList) {
                FoodStoreItemListAPIEntity foodStoreItemListAPIEntity = new FoodStoreItemListAPIEntity(foodStoreItem,appUrlH5Utils);
                foodStoreItemListAPIEntities.add(foodStoreItemListAPIEntity);
            }
        }
        result.setContent(foodStoreItemListAPIEntities, hasMore, null, String.valueOf(page+1));
        return result;
    }

    /**
     * 关键字搜索
     * @param kw
     * @param page
     * @param pageSize
     * @return
     */
    private JsonListResponseEntity<FoodStoreItemListAPIEntity> getFoodListByKwFromDb(String kw, int page, int pageSize){
        JsonListResponseEntity<FoodStoreItemListAPIEntity> result = new JsonListResponseEntity<>();
        List<FoodStoreItemListAPIEntity> foodStoreItemListAPIEntities = new ArrayList<>();
        List<FoodStoreItem> foodStoreItemList = manageFoodStoreService.findFoodStoreItemBaseListByKw(kw, page, pageSize);
        Boolean hasMore = false;
        if (null != foodStoreItemList && foodStoreItemList.size() == pageSize){
            List<FoodStoreItem> foodStoreItemList2 = manageFoodStoreService.findFoodStoreItemBaseListByKw(kw, page + 1, 1);
            if (null != foodStoreItemList2){
                hasMore = true;
            }
        }
        if (foodStoreItemList != null) {
            for (FoodStoreItem foodStoreItem : foodStoreItemList) {
                foodStoreItemListAPIEntities.add(new FoodStoreItemListAPIEntity(foodStoreItem,appUrlH5Utils));
            }
        }
        result.setContent(foodStoreItemListAPIEntities, hasMore, null, String.valueOf(page+1));
        return result;
    }

    @WithoutToken
    @VersionRange
    @GetMapping(path =  "/hotWords")
    public JsonListResponseEntity<String> getSearch(){
        JsonListResponseEntity<String> response = new JsonListResponseEntity<>();
        List<String> hotWords = Lists.newArrayList();
        AppConfig config = appConfigService.findSingleAppConfigByKeyWord(null,null,keyWord);

        if(config!=null && StringUtils.isNotBlank(config.getData())){
            String[] data = config.getData().split(",");
            for(String str : data){
                hotWords.add(str);
            }
        }

        response.setContent(hotWords);
        return response;
    }

}

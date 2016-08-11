package com.wondersgroup.healthcloud.services.foodStore;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;

import java.util.List;

/**
 * Created by longshasha on 16/8/10.
 */
public interface FoodStoreService {

    List<FoodStoreCategory> findCategoryList();

    /**
     * 获取分类下 可显示的食物
     * @param category_id
     * @param page
     * @param pageSize
     * @return
     */
    public List<FoodStoreItem> findFoodStoreItemBaseListByCateId(int category_id, int page, int pageSize);

    /**
     * 搜索关键字 下 可以显示 的食物
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    public List<FoodStoreItem> findFoodStoreItemBaseListByKw(String keyword, int page, int pageSize);

    public FoodStoreItem findHomeStoreItemInfoById(int id);

    public Integer countFoodStoreItemByKw(String keyword);

    public Boolean updateFoodStoreItem(FoodStoreItem foodStoreItem);

    public Boolean multSettingFoodStoreStatus(List<Integer> ids, Boolean is_show);

    public String getServiceVersion();

}

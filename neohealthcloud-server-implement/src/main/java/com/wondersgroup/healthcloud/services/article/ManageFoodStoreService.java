package com.wondersgroup.healthcloud.services.article;

import com.wondersgroup.healthcloud.jpa.entity.article.FoodStoreCategory;
import com.wondersgroup.healthcloud.jpa.entity.article.FoodStoreItem;

import java.util.List;

public interface ManageFoodStoreService {

    public List<FoodStoreCategory> findCategoryList();

    public List<FoodStoreCategory> findCategoryList(Boolean is_show);

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

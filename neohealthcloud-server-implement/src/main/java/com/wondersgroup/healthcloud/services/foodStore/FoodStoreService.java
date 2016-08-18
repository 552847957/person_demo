package com.wondersgroup.healthcloud.services.foodStore;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    List<FoodStoreItem> findFoodStoreItemBaseListByCateId(int category_id, int page, int pageSize);

    /**
     * 搜索关键字 下 可以显示 的食物
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    List<FoodStoreItem> findFoodStoreItemBaseListByKw(String keyword, int page, int pageSize);

    FoodStoreItem findHomeStoreItemInfoById(int id);

    Integer countFoodStoreItemByKw(String keyword);

    Boolean updateFoodStoreItem(FoodStoreItem foodStoreItem);

    Boolean multSettingFoodStoreStatus(List<Integer> ids, Boolean is_show);

    String getServiceVersion();

    FoodStoreCategory findById(int id);

    Page<FoodStoreCategory> findAllListByIsShow(Integer isShow, Pageable pageable);

    Page<FoodStoreCategory> findAllList(Pageable pageable);

    void saveFoodStoreCategory(FoodStoreCategory foodStoreCategory);

    List<Integer[]> findIsShow();

}

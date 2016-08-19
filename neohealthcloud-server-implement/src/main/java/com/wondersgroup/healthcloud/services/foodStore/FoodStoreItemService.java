package com.wondersgroup.healthcloud.services.foodStore;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * Created by shenbin on 16/8/17.
 */
public interface FoodStoreItemService {

    Page<FoodStoreItem> findByExample(FoodStoreItem item, Pageable pageable);

    FoodStoreItem findById(int id);

    void saveFoodStoreItem(FoodStoreItem foodStoreItem);

    List<Integer[]> findIsShow();

    List<Object[]> findCategoryName();;

    Integer updateIsShowByIds(int isShow, Date nowTime, List<Integer> ids);

    Boolean updateFoodStoreItem(FoodStoreItem foodStoreItem);
}

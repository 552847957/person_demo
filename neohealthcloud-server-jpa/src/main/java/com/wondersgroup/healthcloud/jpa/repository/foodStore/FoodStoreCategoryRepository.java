package com.wondersgroup.healthcloud.jpa.repository.foodStore;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/8/10.
 */
public interface FoodStoreCategoryRepository extends JpaRepository<FoodStoreCategory,Integer> {


    @Query("select a from FoodStoreCategory a where a.isShow=1 order by a.rank DESC, a.updateTime desc")
    List<FoodStoreCategory> findVaildList();
}

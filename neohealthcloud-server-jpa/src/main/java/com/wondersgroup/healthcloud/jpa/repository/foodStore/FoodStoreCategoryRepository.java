package com.wondersgroup.healthcloud.jpa.repository.foodStore;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/8/10.
 */
public interface FoodStoreCategoryRepository extends JpaRepository<FoodStoreCategory,Integer> {


    @Query("select a from FoodStoreCategory a where a.isShow=1 order by a.rank DESC, a.updateTime desc")
    List<FoodStoreCategory> findVaildList();

    FoodStoreCategory findById(int id);

    Page<FoodStoreCategory> findByIsShowOrderByRankDescUpdateTimeDesc(Integer isShow, Pageable pageable);

    @Query("select id, categoryName from FoodStoreCategory group by categoryName")
    List<Object[]> findCategoryName();

    @Query("select isShow from FoodStoreCategory group by isShow ")
    List<Integer[]> findIsShow();
}

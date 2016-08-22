package com.wondersgroup.healthcloud.jpa.repository.foodStore;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/8/10.
 */
public interface FoodStoreItemRepository extends JpaRepository<FoodStoreItem, Integer>{


    @Query("select a from FoodStoreItem a where a.categoryId=?1 and a.isShow=1 ")
    List<FoodStoreItem> findListByCateId(int cate_id, Pageable pageable);

    @Query("select a from FoodStoreItem a where a.foodName like %?1% and a.isShow = ?2 ")
    List<FoodStoreItem> findListByFoodNameContainingAndIsShow(String keyword, int isShow, Pageable pageable);

    @Query(" select a from FoodStoreItem a where a.id in ?1")
    List<FoodStoreItem> findByIds(List<Integer> ids);


    @Modifying
    @Transactional
    @Query(" update FoodStoreItem a set a.isShow = ?1 ,a.createTime = ?2 where a.id in ?3")
    Integer updateIsShowByIds(int isShow, Date nowTime, List<Integer> ids);

    @Query("select count(a) from FoodStoreItem a where a.isShow = 1  and  a.foodName like %?1% ")
    Integer countByKw(String keyword);

    FoodStoreItem findById(int id);

    @Query("select isShow from FoodStoreItem group by isShow ")
    List<Integer[]> findIsShow();

}

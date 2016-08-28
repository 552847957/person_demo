package com.wondersgroup.healthcloud.services.foodStore.impl;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import com.wondersgroup.healthcloud.jpa.repository.foodStore.FoodStoreCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.foodStore.FoodStoreItemRepository;
import com.wondersgroup.healthcloud.services.foodStore.FoodStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/8/10.
 */
@Service
public class FoodStoreServiceImpl implements FoodStoreService {

    @Autowired
    private FoodStoreCategoryRepository foodStoreCategoryRepository;

    @Autowired
    private FoodStoreItemRepository foodStoreItemRepository;


    @Override
    public List<FoodStoreCategory> findCategoryList() {
        List<FoodStoreCategory> rt = foodStoreCategoryRepository.findVaildList();
        return rt;
    }

    @Override
    public List<FoodStoreItem> findFoodStoreItemBaseListByCateId(int category_id, int page, int pageSize) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"rank"),new Sort.Order(Sort.Direction.DESC,"updateTime"));

        List<FoodStoreItem> rt = foodStoreItemRepository.findListByCateId(category_id, new PageRequest(page-1,pageSize,sort));
        return rt;
    }

    @Override
    public List<FoodStoreItem> findFoodStoreItemBaseListByKw(String keyword, int page, int pageSize) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"rank"),new Sort.Order(Sort.Direction.DESC,"updateTime"));
        int isShow = 1;
        List<FoodStoreItem> rt = foodStoreItemRepository.findListByFoodNameContainingAndIsShow(keyword, isShow, new PageRequest(page-1, pageSize, sort));
        return rt;
    }

    @Override
    public FoodStoreItem findHomeStoreItemInfoById(int id) {
        FoodStoreItem foodStoreItem = foodStoreItemRepository.findOne(id);
        return foodStoreItem;
    }

    @Override
    public Integer countFoodStoreItemByKw(String keyword) {
        Integer num = foodStoreItemRepository.countByKw(keyword);
        return num;
    }

    @Override
    public Boolean updateFoodStoreItem(FoodStoreItem foodStoreItem) {
        Date now_time = new Date();
        foodStoreItem.setUpdateTime(now_time);
        if (foodStoreItem.getId() > 0){
        }else {
            foodStoreItem.setCreateTime(now_time);
        }
        foodStoreItem = foodStoreItemRepository.saveAndFlush(foodStoreItem);

        return foodStoreItem!=null ? true : false;
    }

    @Override
    public Boolean multSettingFoodStoreStatus(List<Integer> ids, Boolean is_show) {
        if (null == ids || ids.isEmpty()){
            return false;
        }
//        List<FoodStoreItem> foodStoreItemList = foodStoreItemRepository.findByIds(ids);

        int isShow = is_show ? 1 : 0;
        Date nowTime = new Date();
        Integer rt = foodStoreItemRepository.updateIsShowByIds(isShow, nowTime, ids);

        return rt>0 ? true : false;
    }


    @Override
    public String getServiceVersion() {
        return "2016010401";
    }

    @Override
    public FoodStoreCategory findById(int id) {
        return foodStoreCategoryRepository.findById(id);
    }

    @Override
    public Page<FoodStoreCategory> findAllListByIsShow(Integer isShow, Pageable pageable){
        return foodStoreCategoryRepository.findByIsShowOrderByRankDescUpdateTimeDesc(isShow, pageable);
    }

    @Override
    public Page<FoodStoreCategory> findAllList(Pageable pageable) {
        return foodStoreCategoryRepository.findAll(pageable);
    }

    @Override
    public void saveFoodStoreCategory(FoodStoreCategory foodStoreCategory) {
        foodStoreCategoryRepository.saveAndFlush(foodStoreCategory);
    }

    @Override
    public List<Integer[]> findIsShow() {
        return foodStoreCategoryRepository.findIsShow();
    }

}

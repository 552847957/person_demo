package com.wondersgroup.healthcloud.services.article.impl;

import com.wondersgroup.healthcloud.jpa.entity.article.FoodStoreCategory;
import com.wondersgroup.healthcloud.jpa.entity.article.FoodStoreItem;
import com.wondersgroup.healthcloud.services.article.ManageFoodStoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("manageFoodStoreService")
public class ManageFoodStoreServiceImpl implements ManageFoodStoreService {

    @Override
    public List<FoodStoreCategory> findCategoryList() {
        return null;
    }

    @Override
    public List<FoodStoreCategory> findCategoryList(Boolean is_show) {
        return null;
    }

    @Override
    public List<FoodStoreItem> findFoodStoreItemBaseListByCateId(int category_id, int page, int pageSize) {
        return null;
    }

    @Override
    public List<FoodStoreItem> findFoodStoreItemBaseListByKw(String keyword, int page, int pageSize) {
        return null;
    }

    @Override
    public FoodStoreItem findHomeStoreItemInfoById(int id) {
        return null;
    }

    @Override
    public Integer countFoodStoreItemByKw(String keyword) {
        return null;
    }

    @Override
    public Boolean updateFoodStoreItem(FoodStoreItem foodStoreItem) {
        return null;
    }

    @Override
    public Boolean multSettingFoodStoreStatus(List<Integer> ids, Boolean is_show) {
        return null;
    }

    @Override
    public String getServiceVersion() {
        return null;
    }
}

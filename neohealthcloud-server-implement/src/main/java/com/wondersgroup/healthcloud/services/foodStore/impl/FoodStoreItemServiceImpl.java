package com.wondersgroup.healthcloud.services.foodStore.impl;

import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import com.wondersgroup.healthcloud.jpa.repository.foodStore.FoodStoreCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.foodStore.FoodStoreItemRepository;
import com.wondersgroup.healthcloud.services.foodStore.FoodStoreItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by shenbin on 16/8/17.
 */
@Service
public class FoodStoreItemServiceImpl implements FoodStoreItemService {

    @Autowired
    private FoodStoreItemRepository foodStoreItemRepository;

    @Autowired
    private FoodStoreCategoryRepository foodStoreCategoryRepository;

    @Override
    public Page<FoodStoreItem> findByExample(FoodStoreItem item, Pageable pageable) {

        ExampleMatcher exampleMatcher = ExampleMatcher
                .matching()
                .withIgnorePaths("id", "rank")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreNullValues();

        if (item.getIsShow() == -1) {
            exampleMatcher.withIgnorePaths("isShow");
        }
        if (item.getCategoryId() == -1) {
            exampleMatcher.withIgnorePaths("categoryId");
        }

        Example<FoodStoreItem> example = Example.of(item, exampleMatcher);

        return foodStoreItemRepository.findAll(example, pageable);
    }

    @Override
    public FoodStoreItem findById(int id) {
        return foodStoreItemRepository.findById(id);
    }

    @Override
    public void saveFoodStoreItem(FoodStoreItem foodStoreItem) {
        foodStoreItemRepository.saveAndFlush(foodStoreItem);
    }

    @Override
    public List<Integer[]> findIsShow() {
        return foodStoreItemRepository.findIsShow();
    }

    @Override
    public List<Object[]>  findCategoryName() {
        return foodStoreCategoryRepository.findCategoryName();
    }

    @Override
    public Integer updateIsShowByIds(int isShow, Date nowTime, List<Integer> ids) {
        return foodStoreItemRepository.updateIsShowByIds(isShow, nowTime, ids);
    }

    @Override
    public Boolean updateFoodStoreItem(FoodStoreItem foodStoreItem) {
        foodStoreItem.setUpdateTime(new Date());
        foodStoreItem.setCreateTime(new Date());
        return foodStoreItemRepository.save(foodStoreItem) != null ? true : false;
    }

}

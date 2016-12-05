package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleCategoryDto;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by limenghua on 2016/8/21.
 *
 * @author limenghua
 */
public interface CircleCategoryService {

    /**
     * 保存/修改圈子分类
     *
     * @param circleCategory
     * @return
     */
    CircleCategory saveCircleCategory(CircleCategory circleCategory);

    /**
     * 检查分类名称是否存在
     *
     * @param name
     * @return
     */
    boolean ifCategoryNameExist(String name, Integer id);

    /**
     * 搜索圈子分类
     *
     * @return
     */
    List<CircleCategoryDto> searchCategory(String name, String delFlag, int pageNo, int pageSize);

    int searchCategoryCount(String name, String delFlag);

    LinkedHashMap<Integer, String> getCategoryComboMap();

    List<CircleCategoryDto> getCategoryComboList();

    List<CircleCategory> findAllOrderByRankDesc();

    CircleCategory findOne(int id);
}

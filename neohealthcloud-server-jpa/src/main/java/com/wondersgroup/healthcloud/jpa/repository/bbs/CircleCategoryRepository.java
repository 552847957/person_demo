package com.wondersgroup.healthcloud.jpa.repository.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 圈子分类
 */
public interface CircleCategoryRepository extends JpaRepository<CircleCategory, Integer> {

    List<CircleCategory> queryByDelflagOrderByRankDesc(String delflag);
}

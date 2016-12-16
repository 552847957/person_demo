package com.wondersgroup.healthcloud.jpa.repository.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 圈子分类
 */
public interface CircleCategoryRepository extends JpaRepository<CircleCategory, Integer> {

    List<CircleCategory> queryByDelFlagOrderByRankDesc(String delFlag);

    CircleCategory queryByName(String name);

    @Query(nativeQuery = true,value = "select * from tb_bbs_circle_category where name = ?1 and id <> ?2")
    CircleCategory queryByNameAndNotEqualsId(String name,Integer id);

    @Query(nativeQuery = true,value="select max(rank) from tb_bbs_circle_category where name <> ?1")
    Integer getTopRankExcludeName(String name);
}

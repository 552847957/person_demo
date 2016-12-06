package com.wondersgroup.healthcloud.jpa.repository.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by limenghua on 2016/8/11.
 * 圈子banner
 */
public interface CircleBannerRepository extends JpaRepository<CircleBanner, Integer> {

    List<CircleBanner> findByDelFlagOrderByPicOrderDesc(String delFlag);
}

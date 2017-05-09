package com.wondersgroup.healthcloud.services.homeservice;

import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/9.
 */
public interface HomeServices {
    HomeServiceEntity saveHomeService(HomeServiceEntity entity);
    List<HomeServiceEntity> findHomeServiceByCondition(Map paramMap);
}

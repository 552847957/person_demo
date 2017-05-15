package com.wondersgroup.healthcloud.services.homeservice;

import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeUserServiceEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/9.
 */
public interface HomeServices {

    HomeServiceEntity saveHomeService(HomeServiceEntity entity);

    /** 修改
     * @param entity
     * @return
     */
    boolean updateHomeService(HomeServiceEntity entity);

    HomeUserServiceEntity saveHomeUserService(HomeUserServiceEntity entity);

    List<HomeServiceEntity> findHomeServiceByCondition(Map paramMap);

    List<HomeUserServiceEntity> findHomeUserServiceByCondition(Map paramMap);

    /**
     * 查詢我的服務
     * @param paramMap
     * @return
     */
    List<HomeServiceEntity> findMyHomeServices(Map paramMap);

    /**
     * @param oldServices 编辑前的服务
     * @param newServices  编辑后的服务
     * @param userId  用户
     */
    void editMyService(List<HomeServiceEntity> oldServices,List<HomeServiceEntity> newServices,String userId);

    /**
     * 查询所有的版本
     * @return
     */
    List<String> findAllVersions();
}

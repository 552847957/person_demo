package com.wondersgroup.healthcloud.services.homeservice;

import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeTabServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeUserServiceEntity;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeServiceDTO;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeTabServiceDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/9.
 */
public interface HomeServices {


    /***
     * 底部图标 保存
     */
    HomeTabServiceEntity saveHomeTabService(HomeTabServiceEntity entity);

    /**
     * 底部图标 修改
     * @param entity
     * @return
     */
    boolean updateHomeTabService(HomeTabServiceEntity entity);

    /**
     * 查询所有版本号
     * @param version
     * @return
     */
    List<String> findAllHomeTabServiceVersions(String version);

    /**
     * 根据条件查询底部tabl
     * @param paramMap
     * @return
     */
    List<HomeTabServiceEntity> findMyHomeTabService(Map paramMap);




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
    List<String> findAllVersions(String version);

    /**
     * 查询我的基础服务(主要用于编辑状态)
     * @param paramMap
     * @return
     */
    List<HomeServiceDTO> findMyBaseHomeService(Map paramMap);
}

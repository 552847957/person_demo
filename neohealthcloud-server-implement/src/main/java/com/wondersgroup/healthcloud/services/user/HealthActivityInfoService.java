package com.wondersgroup.healthcloud.services.user;

import java.util.List;

import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;

public interface HealthActivityInfoService {

    /**
     * 查询活动列表
     * @param area
     * @param type
     * @return
     */
    List<HealthActivityInfo> getHealthActivityInfos(String area, String type);

    HealthActivityInfo getHealthActivityInfo(String activityid);

    HealthActivityDetail findActivityDetailByAidAndRid(String activityId, String registerId);

    List<HealthActivityInfo> getHealthActivityInfos(String province,String city, String county, Integer status , int pageNo, int pageSize);
    List<HealthActivityInfo> getHealthActivityInfos(String status, String title, String onlineTime, String offlineTime, int pageNo, int pageSize);

}

package com.wondersgroup.healthcloud.services.user;

import java.util.List;
import java.util.Map;

import com.wondersgroup.healthcloud.jpa.entity.activiti.HealthActivityDetail;
import com.wondersgroup.healthcloud.jpa.entity.activiti.HealthActivityInfo;

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

    List<HealthActivityInfo> getHealthActivityInfos(String area, String type, int pageNo, int pageSize);

}

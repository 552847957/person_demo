package com.wondersgroup.healthcloud.services.disease;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2017/5/23.
 */
public interface ScreeningService {
    /**
     * 获取筛查列表数据
     * @param pageNo
     * @param pageSize
     * @param signStatus
     * @param diseaseType
     * @return
     */
    List<Map<String,Object>> findScreening(Integer pageNo, int pageSize, Integer signStatus, String diseaseType, DoctorInfo doctorInfo);

    Boolean remind(List<String> registerIds, String doctorId,Integer type);

    /**
     * 医生筛查提醒数量
     * @param doctorId
     * @return
     */
    Integer getRemindCount(String doctorId);

    Boolean hasToRemindScreened(DoctorInfo doctorInfo);
}

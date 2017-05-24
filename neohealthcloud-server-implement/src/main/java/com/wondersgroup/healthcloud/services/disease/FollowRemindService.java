package com.wondersgroup.healthcloud.services.disease;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2017/5/24.
 */
public interface FollowRemindService {
    List<Map<String,Object>> findFollow(Integer pageNo, int pageSize, Integer signStatus, Integer diseaseType, DoctorInfo doctorInfo, DoctorAccount doctorAccount);
}

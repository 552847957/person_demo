package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceDic;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceRoleMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by jimmy on 16/8/5.
 * 医生服务的service
 */
public interface DoctorServiceService {


    /**
     * 保存医生服务字典
     *
     * @param doctorServiceDic
     */
    void saveDoctorServiceDic(DoctorServiceDic doctorServiceDic);

    Page<DoctorServiceDic> queryDoctorServiceDices(String key,Pageable pageable);

    void saveDoctorServiceRoleMap(DoctorServiceRoleMap serviceRoleMap);

    List<DoctorServiceRoleMap> queryDoctorServiceRoleMap(String key, Pageable pageable);

    int deleteDoctorServiceRoleMap(long id);
}


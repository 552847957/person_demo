package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceDic;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceRoleMap;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceDicRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceRoleMapRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jimmy on 16/8/5.
 */
@Service
public class DoctorServiceServiceImpl implements DoctorServiceService {

    @Autowired
    private DoctorServiceDicRepository doctorServiceDicRepository;

    @Autowired
    DoctorServiceRoleMapRepository doctorServiceRoleMapRepository;

    @Override
    public void saveDoctorServiceDic(DoctorServiceDic serviceDices) {
        doctorServiceDicRepository.save(serviceDices);
    }

    @Override
    public Page<DoctorServiceDic> queryDoctorServiceDices(String key, Pageable pageable) {
        return doctorServiceDicRepository.findAllByNameLike(key, pageable);
    }

    @Override
    public void saveDoctorServiceRoleMap(DoctorServiceRoleMap serviceRoleMap) {
        doctorServiceRoleMapRepository.save(serviceRoleMap);
    }

    @Override
    public List<DoctorServiceRoleMap> queryDoctorServiceRoleMap(String key, Pageable pageable) {
        return doctorServiceRoleMapRepository.findAllByNameLike(key, pageable);
    }

    @Override
    public int deleteDoctorServiceRoleMap(long id) {
        return doctorServiceRoleMapRepository.deleteDoctorServiceRoleMap(id);
    }
}

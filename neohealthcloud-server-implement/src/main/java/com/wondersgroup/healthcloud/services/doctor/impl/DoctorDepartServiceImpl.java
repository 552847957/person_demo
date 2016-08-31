package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartment;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorDepartmentRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
@Service
public class DoctorDepartServiceImpl implements DoctorDepartService {

    @Autowired
    private DoctorDepartmentRepository doctorDepartmentRepository;

    @Override
    public List<DoctorDepartment> queryFirstLevelDepartments() {
        return doctorDepartmentRepository.queryFirstLevelDepartments();
    }

    @Override
    public List<DoctorDepartment> queryDoctorDepartmentsByPid(String id) {
        return doctorDepartmentRepository.queryDoctorDepartmentsByPid(id);
    }
}

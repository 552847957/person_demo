package com.wondersgroup.healthcloud.services.dic.impl;

import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DepartGBRepository;
import com.wondersgroup.healthcloud.services.dic.DepartGbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
@Service
public class DepartGbServiceImpl implements DepartGbService {

    @Autowired
    private DepartGBRepository departGBRepository;

    @Override
    public List<DepartGB> queryFirstLevelDepartments() {
        return departGBRepository.queryFirstLevelDepartments();
    }

    @Override
    public List<DepartGB> queryDoctorDepartmentsByPid(String id) {
        return departGBRepository.queryDoctorDepartmentsByPid(id);
    }
}

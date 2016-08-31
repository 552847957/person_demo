package com.wondersgroup.healthcloud.services.dic;

import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;

import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
public interface DepartGbService {
    List<DepartGB> queryFirstLevelDepartments();

    List<DepartGB> queryDoctorDepartmentsByPid(String id);
}

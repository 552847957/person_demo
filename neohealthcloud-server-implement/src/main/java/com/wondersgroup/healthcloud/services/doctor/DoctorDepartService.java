package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartment;

import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
public interface DoctorDepartService {
    List<DoctorDepartment> queryFirstLevelDepartments();

    List<DoctorDepartment> queryDoctorDepartmentsByPid(String id);
}

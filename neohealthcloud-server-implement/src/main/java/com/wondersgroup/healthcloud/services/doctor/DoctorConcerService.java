package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;

import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
public interface DoctorConcerService {
    Boolean updateDoctorConcerDepartment(String doctorId, String departmentIds);

    List<DepartGB> queryDoctorDepartmentsByDoctorId(String doctorId);

}

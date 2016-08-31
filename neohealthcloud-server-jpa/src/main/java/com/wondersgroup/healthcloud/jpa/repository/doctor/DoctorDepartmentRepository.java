package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
public interface DoctorDepartmentRepository extends JpaRepository<DoctorDepartment,String>{

    @Query(" select a from DoctorDepartment a where a.pid is null")
    List<DoctorDepartment> queryFirstLevelDepartments();

    @Query(" select a from DoctorDepartment a where a.pid = ?1 ")
    List<DoctorDepartment> queryDoctorDepartmentsByPid(String id);
}

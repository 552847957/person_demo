package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartmentRela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by longshasha on 16/8/31.
 */
public interface DoctorDepartmentRelaRepository extends JpaRepository<DoctorDepartmentRela, String> {

    @Query(" delete from DoctorDepartmentRela where doctorid=?1 ")
    Integer deleteById(String doctorId);
}

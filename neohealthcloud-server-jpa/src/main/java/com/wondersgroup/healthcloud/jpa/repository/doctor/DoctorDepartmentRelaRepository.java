package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartmentRela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
public interface DoctorDepartmentRelaRepository extends JpaRepository<DoctorDepartmentRela, String> {

    @Modifying
    @Query(" delete DoctorDepartmentRela  a where a.doctorid=?1 ")
    void deleteById(String doctorId);

    @Query("select a from DoctorDepartmentRela  a where a.doctorid = ?1 ")
    List<DepartGB> queryDoctorDepartmentsByDoctorId(String doctorId);
}

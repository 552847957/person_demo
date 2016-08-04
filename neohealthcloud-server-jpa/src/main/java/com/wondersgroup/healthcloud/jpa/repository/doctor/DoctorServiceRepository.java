package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by longshasha on 16/8/2.
 */
public interface DoctorServiceRepository extends JpaRepository<DoctorServiceEntity,String> {


    @Transactional
    @Modifying
    @Query( " delete from DoctorServiceEntity a where a.doctorId = ?1 ")
    void removeServiceByUid(String registerId);
}

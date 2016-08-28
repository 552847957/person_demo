package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceDic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by shenbin on 16/8/9.
 */
public interface DoctorServiceDicRepository extends JpaRepository<DoctorServiceDic, String> {

    Page<DoctorServiceDic> findByDelFlag(String s, Pageable pageable);

    Page<DoctorServiceDic> findByNameContainingAndDelFlag(String key, String s, Pageable pageable);

    DoctorServiceDic findById(String id);
}

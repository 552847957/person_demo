package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceDic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jimmy on 16/8/4.
 */
public interface DoctorServiceDicRepository extends JpaRepository<DoctorServiceDic, String> {

    Page<DoctorServiceDic> findAllByNameLike(String key, Pageable pageable);
}

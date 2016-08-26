package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by longshasha on 16/8/2.
 */
public interface DoctorInfoRepository extends JpaRepository<DoctorInfo,String> {

    @Transactional
    @Modifying
    @Query(" update DoctorInfo a set a.delFlag = '1' where a.id = ?1 ")
    void closeWonderCloudAccount(String registerId);

    DoctorInfo findById(String id);

    @Query(" select a from DoctorInfo a where a.idcard = ?1 ")
    DoctorInfo findDoctorByPersoncardWithOutDelflag(String idcard);

}

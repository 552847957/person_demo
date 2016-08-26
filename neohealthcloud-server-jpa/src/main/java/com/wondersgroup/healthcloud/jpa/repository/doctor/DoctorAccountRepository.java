package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by longshasha on 16/8/1.
 */
public interface DoctorAccountRepository extends JpaRepository<DoctorAccount, String> {

    @Query(" select a from DoctorAccount a where (a.mobile=?1 or a.loginName=?1) and a.isAvailable='0' and a.delFlag = '0' ")
    DoctorAccount findDoctorByAccount(String account);


    @Query(" select a from DoctorAccount a where a.mobile = ?1 ")
    DoctorAccount findDoctorByMobileWithOutDelfag(String mobile);

    @Transactional
    @Modifying
    @Query(" update DoctorAccount a set a.delFlag = '1' where a.id = ?1 ")
    void closeWonderCloudAccount(String registerId);

}

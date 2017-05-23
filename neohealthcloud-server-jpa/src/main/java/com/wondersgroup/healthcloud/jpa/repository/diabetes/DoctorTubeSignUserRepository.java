package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by longshasha on 17/5/16.
 */
public interface DoctorTubeSignUserRepository extends JpaRepository<DoctorTubeSignUser, String>, JpaSpecificationExecutor<DoctorTubeSignUser> {
    
    @Query(nativeQuery = true, value = "select * from fam_doctor_tube_sign_user where card_number = ?1")
    public DoctorTubeSignUser queryInfoByCard(String card_number);


}

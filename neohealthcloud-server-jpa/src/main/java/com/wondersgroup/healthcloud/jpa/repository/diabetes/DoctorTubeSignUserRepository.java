package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by longshasha on 17/5/16.
 */
public interface DoctorTubeSignUserRepository extends JpaRepository<DoctorTubeSignUser, String>, JpaSpecificationExecutor<DoctorTubeSignUser> {
    
    @Query(nativeQuery = true, value = "select * from fam_doctor_tube_sign_user where card_number = ?1")
    public DoctorTubeSignUser queryInfoByCard(String card_number);

    @Modifying
    @Query(nativeQuery = true,value ="update fam_doctor_tube_sign_user set is_risk = ?2 where card_number = ?1 and card_type = '01'")
    public void updateRisk(String cardNumber,Integer isRisk);
}

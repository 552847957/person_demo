package com.wondersgroup.healthcloud.jpa.repository.user;

import com.wondersgroup.healthcloud.jpa.entity.user.DiseaseRegisterInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DiseaseRegisterInfoRepository extends JpaRepository<DiseaseRegisterInfo,String> {

    @Query("select a from DiseaseRegisterInfo a where a.registerid=?1 and a.delFlag='0' and a.type='3'")
    DiseaseRegisterInfo findByUserId(String registerid);

}

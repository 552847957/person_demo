package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by longshasha on 17/5/26.
 */
public interface NeoFamInterventionRepository extends JpaRepository<NeoFamIntervention, String>, JpaSpecificationExecutor<NeoFamIntervention> {

    @Modifying
    @Transactional
    @Query("update NeoFamIntervention p set p.isDeal = '1', p.doctorInterventionId=?2  where p.registerId = ?1" +
            " and p.type <>'30000' and p.isDeal = '0' and p.delFlag = '0' and to_days(p.warnDate) <= to_days(?3)")
    int setInterventionIsdealAndInterventionId(String registerId,String doctorInterventionId, Date now);

    @Query(nativeQuery=true,value = " select * from neo_fam_intervention a where a.doctor_intervention_id =?1 and a.del_flag ='0' order by a.create_date desc limit 1 ")
    NeoFamIntervention findLatestByInterventionId(String interventionId);
}

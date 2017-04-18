package com.wondersgroup.healthcloud.jpa.repository.remind;

import com.wondersgroup.healthcloud.jpa.entity.medicine.CommonlyUsedMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

public interface CommonlyUsedMedicineRepository extends JpaRepository<CommonlyUsedMedicine, String> {
    @Query("select a from CommonlyUsedMedicine a where a.userId = ?1 and a.type = ?2 order by a.updateTime desc")
    List<CommonlyUsedMedicine> findByUserId(String userId, String type);
    @Query(nativeQuery = true, value = "select a.* from app_tb_commonly_used_medicine a where a.user_id = ?1 and a.type = ?2 order by a.update_time desc limit 0, 5")
    List<CommonlyUsedMedicine> findTop5(String userId, String type);
}
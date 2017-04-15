package com.wondersgroup.healthcloud.jpa.repository.medicine;

import com.wondersgroup.healthcloud.jpa.entity.medicine.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

public interface MedicineRepository extends JpaRepository<Medicine, String> {
    @Query("select a from Medicine a where a.delFlag = '0' order by a.id asc")
    List<Medicine> findAllByOrderByIdAsc();
}
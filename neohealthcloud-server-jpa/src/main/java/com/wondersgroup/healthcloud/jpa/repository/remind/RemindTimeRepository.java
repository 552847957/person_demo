package com.wondersgroup.healthcloud.jpa.repository.remind;

import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/04/11.
 */

public interface RemindTimeRepository extends JpaRepository<RemindTime, String> {
    @Query("select a from RemindTime a where a.remindId = ?1 and a.delFlag = '0' order by a.remindTime asc")
    List<RemindTime> findByRemindId(String remindId);
}
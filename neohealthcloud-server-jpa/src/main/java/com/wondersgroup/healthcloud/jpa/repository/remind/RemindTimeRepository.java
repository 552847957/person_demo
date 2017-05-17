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
    
    @Query(nativeQuery = true,value="SELECT t2.* FROM app_tb_remind t1 INNER JOIN app_tb_remind_time t2 ON t1.id=t2.remind_id WHERE t1.user_id = ?1 AND t1.del_flag=0 ORDER BY t2.remind_time DESC,t1.update_time ASC")
    List<RemindTime> findRemindByUid(String uid);
}
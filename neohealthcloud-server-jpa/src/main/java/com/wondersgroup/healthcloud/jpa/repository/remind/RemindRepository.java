package com.wondersgroup.healthcloud.jpa.repository.remind;

import com.wondersgroup.healthcloud.jpa.entity.remind.Remind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/04/11.
 */

public interface RemindRepository extends JpaRepository<Remind, String> {

    @Query( nativeQuery = true, value = "select * from app_tb_remind a where a.user_id = ?1 order by a.update_time desc limit ?2, ?3")
    List<Remind> findByUserId(String userId, int startRowNo, int pageSize);
}
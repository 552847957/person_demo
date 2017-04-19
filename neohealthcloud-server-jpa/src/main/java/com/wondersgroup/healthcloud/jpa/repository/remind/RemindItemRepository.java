package com.wondersgroup.healthcloud.jpa.repository.remind;

import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/04/11.
 */

public interface RemindItemRepository extends JpaRepository<RemindItem, String> {
    @Query("select a from RemindItem a where a.remindId = ?1 and a.delFlag = '0' order by a.updateTime desc")
    List<RemindItem> findByRemindId(String remindId);
}
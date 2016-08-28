package com.wondersgroup.healthcloud.jpa.repository.activity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;

public interface HealthActivityInfoRepository extends JpaRepository<HealthActivityInfo, String> {

    @Query(value = "select hai from HealthActivityInfo as hai where (hai.province=?1 or hai.city=?1 or hai.county=?1) and online_status = '1' and type=?2 and hai.endtime >= current_date()  and hai.delFlag=0  order by starttime desc")
    List<HealthActivityInfo> findActivitiesByAreaAndType(String area, String type);

    @Query(value = "select hai from HealthActivityInfo as hai where (hai.province=?1 or hai.city=?1 or hai.county=?1) and online_status = '1' and type!=0 and hai.endtime >= current_date()  and hai.delFlag=0  order by starttime desc")
    List<HealthActivityInfo> findActivitiesByArea(String area);

    @Query(nativeQuery = true, value = "select * from app_tb_healthactivity_info where activityid in" 
            +"("
                +"select activityid from app_tb_healthactivity_detail where registerid = ?1 and del_flag = '0'"
            +") and del_flag = '0' and starttime > now() ORDER BY starttime desc limit 1")
    HealthActivityInfo findOneActivityByRegId(String regId);
    
}

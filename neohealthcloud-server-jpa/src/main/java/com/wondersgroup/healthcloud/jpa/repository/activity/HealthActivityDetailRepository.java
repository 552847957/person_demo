package com.wondersgroup.healthcloud.jpa.repository.activity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;

public interface HealthActivityDetailRepository extends JpaRepository<HealthActivityDetail, String> {
    @Query(value = "select had from HealthActivityDetail as had where had.registerid=?1 and had.delFlag=0 order by ifnull(had.evaluatetime,'2999-01-01') desc ")
    List<HealthActivityDetail> findActivitiesByRegisterId(String registerId);

    @Query(value = "select count(1) from HealthActivityDetail as had where had.activityid=?1 and had.delFlag=0")
    Integer findActivityRegistrationByActivityId(String activityId);

    @Query(value = "select count(1) from HealthActivityDetail as h where h.activityid=?1 and h.registerid=?2 and h.delFlag=0")
    String findActivityDetailByActivityIdAndRegisterid(String activityId, String registerId);

    @Query(value = "select had from HealthActivityDetail as had where had.activityid=?1 and had.registerid=?2 and had.delFlag=0")
    HealthActivityDetail findActivityDetailByAidAndRid(String activityId, String registerId);

    @Query(value = "select count(1) from HealthActivityDetail as had where had.activityid=?1 and had.registerid=?2 and had.delFlag=0")
    Integer findActivityDetailByAidAndRidNum(String activityId, String registerId);

    @Transactional
    @Modifying
    @Query(value = "update HealthActivityDetail had set had.evaluate=?1,had.evalatememo=?2,had.evaluatetime=?3 where had.id=?4")
    int updateActivityDetailByAidAndRid(String evaluate, String evalatememo, String evaluatetime, String id);

}

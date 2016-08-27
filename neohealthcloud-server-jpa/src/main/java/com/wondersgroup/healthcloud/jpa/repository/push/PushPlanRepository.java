package com.wondersgroup.healthcloud.jpa.repository.push;

import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/8/26.
 */
public interface PushPlanRepository extends JpaRepository<PushPlan, Integer>,JpaSpecificationExecutor<PushPlan> {

    @Transactional
    @Modifying
    @Query("update PushPlan p set p.status = 5 where  p.status = 0 and p.planTime <= ?1")
    void updateOverDuePlan(Date date);
}

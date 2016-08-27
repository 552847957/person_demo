package com.wondersgroup.healthcloud.jpa.repository.push;

import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by zhuchunliu on 2016/8/26.
 */
public interface PushPlanRepository extends JpaRepository<PushPlan, Integer>,JpaSpecificationExecutor<PushPlan> {
}

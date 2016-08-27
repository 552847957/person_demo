package com.wondersgroup.healthcloud.services.push;

import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/8/27.
 */
public interface PushPlanService {

    Page<PushPlan> findAll(int number, int size, Map parameter);
}

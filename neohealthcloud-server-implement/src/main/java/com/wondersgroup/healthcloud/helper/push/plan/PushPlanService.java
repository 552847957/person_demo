package com.wondersgroup.healthcloud.helper.push.plan;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhuchunliu on 2016/8/27.
 */
@Service
public class PushPlanService {

    @Autowired
    private PushPlanRepository pushPlanRepo;

    @Autowired
    private UserRepository userRepo;

    public Page<PushPlan> findAll(int number, int size, final Map parameter, final User user) {

        //将pushtime到期，但是未审核的推送设置为过期
        pushPlanRepo.updateOverDuePlan(new Date());

        Specification<PushPlan> specification = new Specification<PushPlan>() {
            @Override
            public Predicate toPredicate(Root<PushPlan> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = Lists.newArrayList();
                if(null != parameter) {
                    if (parameter.containsKey("tagid") && StringUtils.isNotEmpty(parameter.get("tagid").toString())) {
                        list.add(criteriaBuilder.equal(root.get("target").as(String.class), parameter.get("tagid").toString()));
                    }
                    if (parameter.containsKey("pushstatus") && StringUtils.isNotEmpty(parameter.get("pushstatus").toString())) {
                        list.add(criteriaBuilder.equal(root.get("status").as(String.class), parameter.get("pushstatus").toString()));
                    }
                    if (parameter.containsKey("startTime") && StringUtils.isNotEmpty(parameter.get("startTime").toString())) {
                        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("planTime").as(String.class), parameter.get("startTime").toString()));
                    }
                    if (parameter.containsKey("endTime") && StringUtils.isNotEmpty(parameter.get("endTime").toString())) {
                        list.add(criteriaBuilder.lessThanOrEqualTo(root.get("planTime").as(String.class), parameter.get("endTime").toString()));
                    }
                    if (parameter.containsKey("type") && StringUtils.isNotEmpty(parameter.get("type").toString())) {
                        list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), parameter.get("type").toString()));
                    }
                    list.add(criteriaBuilder.equal(root.get("area").as(String.class),user.getMainArea()));//查找同意区域的
                }
                criteriaQuery.where(list.toArray(new Predicate[list.size()]));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
                return criteriaQuery.getRestriction();
            }
        };
        Pageable pageable = new PageRequest(number,size);


        return pushPlanRepo.findAll(specification,pageable);
    }


}

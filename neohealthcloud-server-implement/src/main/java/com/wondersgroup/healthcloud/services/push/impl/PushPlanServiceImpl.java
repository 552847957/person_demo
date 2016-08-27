package com.wondersgroup.healthcloud.services.push.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;
import com.wondersgroup.healthcloud.services.push.PushPlanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/8/27.
 */
@Service
public class PushPlanServiceImpl implements PushPlanService{

    @Autowired
    private PushPlanRepository pushPlanRepo;

    @Override
    public Page<PushPlan> findAll(int number, int size, final Map parameter) {
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

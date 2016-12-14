package com.wondersgroup.healthcloud.helper.push.plan;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.jpa.constant.AppPushConstant;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;

/**
 * Created by zhuchunliu on 2016/8/27.
 */
@Service
public class PushPlanService {
    private Logger logger = LoggerFactory.getLogger(PushPlanService.class);
    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;
    
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
    
    @Transactional
    public String cancel(Integer id,Integer preStatus,String jobClientUrl,String topicUrl,Integer status){
        PushPlan pushPlan = pushPlanRepo.findOne(id);
        if((1 == status || 4 == status) && 0 != pushPlan.getStatus()){//通过
            return "问题非待审核状态";
        }
        pushPlan.setStatus(status);
        pushPlan.setUpdateTime(new Date());
        pushPlanRepo.save(pushPlan);
        //取消定时任务
        if(1 == preStatus) {//之前状态为待推送状态，则可以取消定时任务
            Request build=null;
            if(pushPlan.getType()==AppPushConstant.PushType.ARTICLE){
                build = new RequestBuilder().delete().url(jobClientUrl + "/api/healthcloud/push").param("planId", id.toString()).build();
            }else if(pushPlan.getType()==AppPushConstant.PushType.TOPIC){
                build = new RequestBuilder().delete().url(topicUrl + "/api/healthcloud/push").param("planId", id.toString()).build();
            }
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper)httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);
            JsonNode result = response.convertBody();
            logger.error("定时任务(pushId = "+id+")取消成功，返回结果"+result);
    }
     return "取消成功";   
}

}

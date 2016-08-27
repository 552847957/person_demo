package com.wondersgroup.healthcloud.services.push.impl;

import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.entity.push.UserPushTag;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.UserPushTagRepository;
import com.wondersgroup.healthcloud.services.push.PushTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuchunliu on 2016/8/26.
 */
@Service
public class PushTagServiceImpl implements PushTagService{
    @Autowired
    private UserPushTagRepository userPushTagRepo;

    @Autowired
    private PushTagRepository pushTagRepo;

    @Override
    public Boolean bindTag(String tagname, String uids) {
        PushTag pushTag = pushTagRepo.findByName(tagname);
        Boolean isNew = false;
        if(null == pushTag){
            pushTag = new PushTag();
            pushTag.setTagname(tagname);
            pushTag.setUpdatetime(new Date());
            pushTag = pushTagRepo.save(pushTag);
            isNew = true;
        }

        for(String uid : uids.split(",")){

            if(isNew || userPushTagRepo.getCount(pushTag.getTagid(),uid) == 0){
                UserPushTag userPushTag = new UserPushTag(pushTag.getTagid(),uid);
                userPushTagRepo.save(userPushTag);
            }
        }
        return true;
    }

    @Override
    public List<PushTag> findAll() {
        Specification<PushTag> specification = new Specification<PushTag>() {
            @Override
            public Predicate toPredicate(Root<PushTag> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updatetime").as(Date.class)));
                return criteriaQuery.getRestriction();
            }
        };
        return pushTagRepo.findAll(specification);
    }
}

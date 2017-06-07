package com.wondersgroup.healthcloud.services.disease.impl;

import static com.google.common.collect.Iterables.toArray;

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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.constant.DiseaseTypeConstant;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentCondition;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;

/**
 * Created by limenghua on 2017/6/6.
 *
 * @author limenghua
 */
@Service("doctorTubeSignUserService")
public class DoctorTubeSignUserServiceImpl implements DoctorTubeSignUserService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorTubeSignUserServiceImpl.class);
    @Autowired
    private DoctorTubeSignUserRepository doctorTubeSignUserRepository;

    @Override
    public Page<DoctorTubeSignUser> search(final ResidentCondition user, int page) {
        logger.info(String.format("doctorTubeSignUserRepository:[%s]", doctorTubeSignUserRepository));
        Page<DoctorTubeSignUser> list = doctorTubeSignUserRepository.findAll(new Specification<DoctorTubeSignUser>() {
            @Override
            public Predicate toPredicate(Root<DoctorTubeSignUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Map<String, String> equalMap = Maps.newHashMap();
                Map<String, String> notEqualMap = Maps.newHashMap();

                // 是否签约
                if (user.getSigned() != null) {
                    equalMap.put("signStatus", String.valueOf(user.getSigned()));
                }

                if (StringUtils.isNotBlank(user.getDiseaseType())) {
                    String[] diseaseArray = user.getDiseaseType().split(",");
                    for (String s : diseaseArray) {
                        switch (s) {
                            case DiseaseTypeConstant.APO:
                                equalMap.put("apoType", "1");
                                break;
                            case DiseaseTypeConstant.DIABETES:
                                notEqualMap.put("diabetesType", "0");
                                break;
                            case DiseaseTypeConstant.HYP:
                                notEqualMap.put("hypType", "0");
                                break;
                        }
                    }// end for
                }// end if

                List<Predicate> predicates = Lists.newArrayList();
                // equal
                for (String s : equalMap.keySet()) {
                    Predicate condition = cb.equal(root.<String>get(s), equalMap.get(s));
                    predicates.add(condition);
                }
                // notEqual
                for (String s : notEqualMap.keySet()) {
                    Predicate condition = cb.notEqual(root.<String>get(s), notEqualMap.get(s));
                    predicates.add(condition);
                }

                Predicate result = predicates.isEmpty() ? cb.conjunction() : cb.and(toArray(predicates, Predicate.class));
                return result;
            }// end inner method
        }, new PageRequest(page, 20, new Sort(Sort.Direction.ASC, "name")));// end method


        return list;
    }// end outer method

    @Override
    public List<ResidentInfoDto> queryByGroup(Integer groupId, int page, int pageSize) {
        return null;
    }
}

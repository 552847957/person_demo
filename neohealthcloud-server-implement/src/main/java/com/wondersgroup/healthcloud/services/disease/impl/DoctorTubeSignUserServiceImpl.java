package com.wondersgroup.healthcloud.services.disease.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.jpa.constant.CommonConstant;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.constant.DiseaseTypeConstant;
import com.wondersgroup.healthcloud.services.disease.constant.PeopleTypeConstant;
import com.wondersgroup.healthcloud.services.disease.constant.ResidentConstant;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentCondition;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;
import com.wondersgroup.healthcloud.services.group.PatientGroupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.toArray;

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
    @Autowired
    private PatientGroupService patientGroupService;

    @Override
    public Page<DoctorTubeSignUser> search(final ResidentCondition user) {

        Page<DoctorTubeSignUser> list = doctorTubeSignUserRepository.findAll(new Specification<DoctorTubeSignUser>() {
            @Override
            public Predicate toPredicate(Root<DoctorTubeSignUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = Lists.newArrayList();
                Map<String, String> equalMap = Maps.newHashMap();
                Map<String, String> notEqualMap = Maps.newHashMap();

                // 是否签约
                if (user.getSigned() != null) {
                    equalMap.put("signStatus", String.valueOf(user.getSigned()));
                }

                // 慢病种类筛选
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

                // 人群分类
                if (StringUtils.isNotBlank(user.getPeopleType())) {
                    switch (user.getPeopleType()) {
                        case PeopleTypeConstant.RISK:
                            // 高危人群
                            equalMap.put("isRisk", "1");
                            break;
                        case PeopleTypeConstant.DISEASE:
                            // 疾病人群
                            Predicate condition = cb.or(cb.notEqual(root.<String>get("apoType"), "0"), cb.notEqual(root.<String>get("diabetesType"), "0"), cb.notEqual(root.<String>get("hypType"), "0"));
                            predicates.add(condition);
                            break;
                        case PeopleTypeConstant.HEALTHY:
                            // 健康人群
                            Predicate conditionH = cb.and(cb.equal(root.<String>get("apoType"), "0"), cb.equal(root.<String>get("diabetesType"), "0"), cb.equal(root.<String>get("hypType"), "0"), cb.equal(root.<String>get("isRisk"), "0"));
                            predicates.add(conditionH);
                            break;
                    }// end switch
                }// end if


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
        }, new PageRequest(user.getPage() - 1, user.getPageSize(), new Sort(Sort.Direction.ASC, "name")));// end method


        return list;
    }// end outer method

    @Override
    public List<ResidentInfoDto> queryByGroup(Integer groupId, int page, int pageSize) {
        List<ResidentInfoDto> dtoList = Lists.newArrayList();
        List<String> list = patientGroupService.getUserIdsByGroupId(groupId);
        // 注意:分页下标从0开始
        Page<DoctorTubeSignUser> pageData = doctorTubeSignUserRepository.queryByDelFlagAndIdIn(CommonConstant.USED_DEL_FLAG, list, new PageRequest(page - 1, pageSize));
        List<DoctorTubeSignUser> tubeSignUserList = pageData.getContent();
        if (tubeSignUserList != null && tubeSignUserList.size() > 0) {
            for (DoctorTubeSignUser doctorTubeSignUser : tubeSignUserList) {
                ResidentInfoDto dto = copyResidentInfo(doctorTubeSignUser);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public Page<DoctorTubeSignUser> queryByGroupId(Integer groupId, int page, int pageSize) {
        List<String> list = patientGroupService.getUserIdsByGroupId(groupId);
        Page<DoctorTubeSignUser> pageData = null;
        if (list.size() > 0) {
            // 注意:分页下标从0开始
            pageData = doctorTubeSignUserRepository.queryByDelFlagAndIdIn(CommonConstant.USED_DEL_FLAG, list, new PageRequest(page - 1, pageSize));
        }
        return pageData;
    }

    @Override
    public List<ResidentInfoDto> pageDataToDtoList(Page<DoctorTubeSignUser> pageData) {
        List<ResidentInfoDto> dtoList = Lists.newArrayList();

        if (pageData != null && pageData.getContent() != null) {
            List<DoctorTubeSignUser> tubeSignUserList = pageData.getContent();

            if (tubeSignUserList.size() > 0) {

                for (DoctorTubeSignUser doctorTubeSignUser : tubeSignUserList) {
                    ResidentInfoDto dto = copyResidentInfo(doctorTubeSignUser);
                    dtoList.add(dto);
                }
            }// end if
        }
        return dtoList;
    }

    private ResidentInfoDto copyResidentInfo(DoctorTubeSignUser doctorTubeSignUser) {
        ResidentInfoDto dto = new ResidentInfoDto();
        BeanUtils.copyProperties(doctorTubeSignUser, dto);
        dto.setRegisterId(doctorTubeSignUser.getId());

        // 高糖脑危标签设置
        if (StringUtils.isNotBlank(doctorTubeSignUser.getHypType())) {
            if (!ResidentConstant.NORMAL.equals(doctorTubeSignUser.getHypType())) {
                dto.setHypType(true);
            }
        }
        if (StringUtils.isNotBlank(doctorTubeSignUser.getDiabetesType())) {
            if (!ResidentConstant.NORMAL.equals(doctorTubeSignUser.getDiabetesType())) {
                dto.setDiabetesType(true);
            }
        }
        if (StringUtils.isNotBlank(doctorTubeSignUser.getApoType())) {
            if (!ResidentConstant.NORMAL.equals(doctorTubeSignUser.getApoType())) {
                dto.setApoType(true);
            }
        }
        if (StringUtils.isNotBlank(doctorTubeSignUser.getIsRisk())) {
            if (!ResidentConstant.NORMAL.equals(doctorTubeSignUser.getIsRisk())) {
                dto.setIsRisk(true);
            }
        }
        return dto;
    }
}

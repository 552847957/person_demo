package com.wondersgroup.healthcloud.services.disease.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.constant.CommonConstant;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.group.SignUserDoctorGroup;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.TubeDiabetes;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.jpa.repository.group.SignUserDoctorGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.AddressRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.TubeDiabetesRepository;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.constant.DiseaseTypeConstant;
import com.wondersgroup.healthcloud.services.disease.constant.PeopleTypeConstant;
import com.wondersgroup.healthcloud.services.disease.constant.ResidentConstant;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentCondition;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

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
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DoctorTubeSignUserRepository doctorTubeSignUserRepository;
    @Autowired
    private PatientGroupService patientGroupService;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private SignUserDoctorGroupRepository signUserDoctorGroupRepository;
    @Autowired
    private TubeDiabetesRepository tubeDiabetesRepository;
    @Autowired
    private DictCache dictCache;
    @Autowired
    private DoctorService doctorService;

    @Override
    public Page<DoctorTubeSignUser> search(final ResidentCondition user) {

        Page<DoctorTubeSignUser> list = doctorTubeSignUserRepository.findAll(new Specification<DoctorTubeSignUser>() {
            @Override
            public Predicate toPredicate(Root<DoctorTubeSignUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = Lists.newArrayList();
                Map<String, String> equalMap = Maps.newHashMap();
                Map<String, String> notEqualMap = Maps.newHashMap();

                // 根据医生id获取身份证号码
                DoctorInfo doctorInfo = doctorService.getDoctorInfoByUid(user.getFamId());
                String idCard = doctorInfo.getIdcard();

                if (StringUtils.isNotBlank(idCard)) {
                    /**
                     * 1，属于该医生签约居民
                     2，属于该医生G端在管人群且非其他医生签约居民
                     3，C端实名认证用户地址匹配出符合管辖范围（只匹配到区，即C端填写地址属于闵行区的所有与上述两条件去重后的用户会出现在闵行区的所有医生的居民列表）。
                     (第3条,去掉已签约的用户)
                     */
                    Predicate condition = cb.or(cb.equal(root.<String>get("signDoctorPersoncard"), idCard),
                            cb.and(cb.equal(root.<String>get("tubeDoctorpersoncard"), idCard), cb.isNull(root.<String>get("signDoctorPersoncard"))),
                            cb.and(root.<String>get("cardNumber").in(doctorService.getResidentListByArea(user.getFamId())), cb.isNull(root.<String>get("signDoctorPersoncard"))));
                    predicates.add(condition);
                }

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
                                notEqualMap.put("apoType", "0");
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
    public List<DoctorTubeSignUser> kwSearchList(String doctorId, String kw, int page, int pageSize) {
        // List<DoctorTubeSignUser> kwSearchList = doctorTubeSignUserRepository.searchByKw(kw, (page - 1) * pageSize, pageSize);
        String inSqlStr = getInString(doctorId);

        // 根据医生id获取身份证号码
        DoctorInfo doctorInfo = doctorService.getDoctorInfoByUid(doctorId);
        String idCard = doctorInfo.getIdcard();
        String sql = "select * from fam_doctor_tube_sign_user f where del_flag = '0' " +
                " and (f.sign_doctor_personcard = ('%s')  or (f.tube_doctor_personcard = '%s' and f.sign_doctor_personcard is null) " +
                " or ( f.card_number in (%s) and f.sign_doctor_personcard is null )) and f.name like '%%%s%%' order by \n" +
                "(case\n" +
                "when f.name = '%s' then 1 \n" +
                "when f.name like '%s%%' then 2\n" +
                "when f.name like '%%%s' then 3\n" +
                "when f.name like '%%%s%%' then 4  \n" +
                "else 0\n" +
                "end ) limit %s,%s";
        sql = String.format(sql, idCard, idCard, inSqlStr, kw, kw, kw, kw, kw, (page - 1) * pageSize, pageSize);
        List<DoctorTubeSignUser> kwSearchList = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(DoctorTubeSignUser.class));
        return kwSearchList;
    }

    private String getInString(String doctorId) {
        List<String> cardList = doctorService.getResidentListByArea(doctorId);
        StringBuffer inSql = new StringBuffer("");
        for (String s : cardList) {
            inSql.append("'");
            inSql.append(s);
            inSql.append("',");
        }
        String inSqlStr = inSql.toString();
        if (inSqlStr.length() > 1) {
            inSqlStr = inSqlStr.substring(0, inSqlStr.length() - 1);
        }
        return inSqlStr;
    }

    @Override
    public long kwSearchCount(String doctorId, String kw) {
        String inSqlStr = getInString(doctorId);

        // 根据医生id获取身份证号码
        DoctorInfo doctorInfo = doctorService.getDoctorInfoByUid(doctorId);
        String idCard = doctorInfo.getIdcard();
        String sql = "select count(f.id) from fam_doctor_tube_sign_user f where del_flag = '0' " +
                " and (f.sign_doctor_personcard = ('%s')  or (f.tube_doctor_personcard = '%s' and f.sign_doctor_personcard is null) " +
                " or ( f.card_number in (%s) and f.sign_doctor_personcard is null ) )and f.name like '%%%s%%' order by \n" +
                "(case\n" +
                "when f.name = '%s' then 1 \n" +
                "when f.name like '%s%%' then 2\n" +
                "when f.name like '%%%s' then 3\n" +
                "when f.name like '%%%s%%' then 4  \n" +
                "else 0\n" +
                "end )";
        sql = String.format(sql, idCard, idCard, inSqlStr, kw, kw, kw, kw, kw);
        long result = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);
        return result;
    }

    @Override
    public Page<DoctorTubeSignUser> kwSearch(final String kw, int page, int pageSize) {
        return null;
    }

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
    public List<ResidentInfoDto> pageDataToDtoList(String doctorId, Page<DoctorTubeSignUser> pageData) {
        List<ResidentInfoDto> dtoList = Lists.newArrayList();

        if (pageData != null && pageData.getContent() != null) {
            List<DoctorTubeSignUser> tubeSignUserList = pageData.getContent();

            if (tubeSignUserList.size() > 0) {

                for (DoctorTubeSignUser doctorTubeSignUser : tubeSignUserList) {
                    ResidentInfoDto dto = copyResidentInfo(doctorId, doctorTubeSignUser);
                    dtoList.add(dto);
                }
            }// end if
        }
        return dtoList;
    }

    @Override
    public List<ResidentInfoDto> dbListToDtoList(String doctorId, List<DoctorTubeSignUser> dbList) {
        List<ResidentInfoDto> dtoList = Lists.newArrayList();
        if (dbList != null && dbList.size() > 0) {
            for (DoctorTubeSignUser doctorTubeSignUser : dbList) {
                ResidentInfoDto dto = copyResidentInfo(doctorId, doctorTubeSignUser);
                dtoList.add(dto);
            }
        }// end if
        return dtoList;
    }

    @Override
    public List<ResidentInfoDto> sortTheGroupedResidents(List<ResidentInfoDto> residentInfoDtoList, Integer groupId) {
        List<ResidentInfoDto> newList = Lists.newArrayList();
        List<String> userIdList = patientGroupService.getUserIdsByGroupId(groupId);
        if (userIdList != null && userIdList.size() > 0) {
            for (String userId : userIdList) {
                for (ResidentInfoDto residentInfoDto : residentInfoDtoList) {
                    if (userId.equals(residentInfoDto.getRegisterId())) {
                        newList.add(residentInfoDto);
                        break;
                    }
                }// END FOR
            }// end for
        }
        return newList;
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

        // 是否实名
        if (StringUtils.isNotBlank(doctorTubeSignUser.getIdentifytype())) {
            if (ResidentConstant.IDENTIFIED.equals(doctorTubeSignUser.getIdentifytype())) {
                dto.setIdentifyType(true);
            }
        }

        // 设置地址信息
        Address address = addressRepository.queryFirst1ByDelFlagAndPersoncard(doctorTubeSignUser.getCardNumber());
        if (address != null) {
            String adr = String.format("%s%s%s%s",
                    //StringUtils.trimToEmpty(dictCache.queryArea(address.getProvince())),
                    //StringUtils.trimToEmpty(dictCache.queryArea(address.getCity())),
                    StringUtils.trimToEmpty(dictCache.queryArea(address.getCounty())),
                    StringUtils.trimToEmpty(dictCache.queryArea(address.getTown())),
                    StringUtils.trimToEmpty(dictCache.queryArea(address.getCommittee())),
                    StringUtils.trimToEmpty(address.getOther()));
            dto.setAddress(adr);
        }
        // 不是签约用户,同时是G端在管用户的,去在管表查地址
        if ("0".equals(doctorTubeSignUser.getSignStatus()) && doctorTubeSignUser.getTubeType() == 1) {
            String adr = getGUserAddress(doctorTubeSignUser.getCardNumber());
            dto.setAddress(adr);
        }

        // 是否分组
        SignUserDoctorGroup signUserDoctorGroup = signUserDoctorGroupRepository.queryFirst1ByDelFlagAndUid(CommonConstant.USED_DEL_FLAG, doctorTubeSignUser.getId());
        if (signUserDoctorGroup != null) {
            dto.setIfGrouped(true);
        } else {
            dto.setIfGrouped(false);
        }

        return dto;
    }

    @Override
    public String getGUserAddress(String cardNumber) {
        TubeDiabetes tubeDiabetes = tubeDiabetesRepository.queryFirst1ByZjhm(cardNumber);
        String adr = String.format("%s%s%s%s%s",
                //StringUtils.trimToEmpty(dictCache.queryArea(address.getProvince())),
                //StringUtils.trimToEmpty(dictCache.queryArea(address.getCity())),
                StringUtils.trimToEmpty(dictCache.queryArea(tubeDiabetes.getJzdXia())),
                StringUtils.trimToEmpty(dictCache.queryArea(tubeDiabetes.getJzdXng())),
                StringUtils.trimToEmpty(dictCache.queryArea(tubeDiabetes.getJzdVlg())),
                StringUtils.trimToEmpty(tubeDiabetes.getJzdCun()),
                StringUtils.trimToEmpty(tubeDiabetes.getJzdMph()));
        return adr;
    }

    /**
     * 是否分组,需要限制医生id
     * @param doctorId
     * @param doctorTubeSignUser
     * @return
     */
    private ResidentInfoDto copyResidentInfo(String doctorId, DoctorTubeSignUser doctorTubeSignUser) {
        ResidentInfoDto dto = copyResidentInfo(doctorTubeSignUser);
        // 分组重新查询
        if (StringUtils.isNotBlank(doctorId)) {
            // 是否分组
            SignUserDoctorGroup signUserDoctorGroup = signUserDoctorGroupRepository.queryByDoctorIdUid(doctorId, doctorTubeSignUser.getId());
            if (signUserDoctorGroup != null) {
                dto.setIfGrouped(true);
            } else {
                dto.setIfGrouped(false);
            }
            // 是否签约
            if (StringUtils.isNotBlank(doctorTubeSignUser.getSignStatus())) {
                if ("1".equals(doctorTubeSignUser.getSignStatus())) {
                    // 判断是否属于该医生的签约
                    // 根据医生id获取身份证号码
                    DoctorInfo doctorInfo = doctorService.getDoctorInfoByUid(doctorId);
                    String idCard = doctorInfo.getIdcard();
                    if (StringUtils.isNotBlank(doctorTubeSignUser.getSignDoctorPersoncard())){
                        // 该签约用户的签约医生身份证和当前查询的医生身份证一致,说明是该医生的签约用户,打上"签"标签
                        if(doctorTubeSignUser.getSignDoctorPersoncard().equals(idCard)){
                            dto.setSignStatus(true);
                        }// end if
                    }// end if
                }
            }
        }// end if 判断医生Id是否存在

        return dto;
    }

}

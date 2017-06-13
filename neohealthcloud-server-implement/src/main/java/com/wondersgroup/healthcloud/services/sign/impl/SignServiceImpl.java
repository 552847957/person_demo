package com.wondersgroup.healthcloud.services.sign.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.constant.CommonConstant;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.entity.group.SignUserDoctorGroup;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.jpa.repository.group.SignUserDoctorGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.AddressRepository;
import com.wondersgroup.healthcloud.services.disease.constant.DiseaseTypeConstant;
import com.wondersgroup.healthcloud.services.disease.constant.PeopleTypeConstant;
import com.wondersgroup.healthcloud.services.disease.constant.ResidentConstant;
import com.wondersgroup.healthcloud.services.sign.SignDTO;
import com.wondersgroup.healthcloud.services.sign.SignService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ZZX on 2017/6/9.
 */
@Service
public class SignServiceImpl implements SignService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SignUserDoctorGroupRepository sudgRepo;

    @Autowired
    private DictCache dictCache;

    @Override
    public List<SignDTO> userLists(String name, String diseaseType, String peopleType, int pageNo, int pageSize) {

        StringBuffer sql = new StringBuffer();

        // SELECT
        sql.append("SELECT a.* FROM fam_doctor_tube_sign_user a");

        // WHERE
        sql.append(" WHERE 1 = 1");
        // name 搜索
        if (StringUtils.isNotEmpty(name)) {
            sql.append(" AND LOCATE('" + name + "', a.name) > 0");
        }
        // 慢病种类搜索
        if (StringUtils.isNotEmpty(diseaseType)) {
            String[] diseaseArray = diseaseType.split(",");
            for (String s : diseaseArray) {
                switch (s) {
                    case DiseaseTypeConstant.APO:
                        sql.append(" AND a.apo_type <> '0'");
                        break;
                    case DiseaseTypeConstant.DIABETES:
                        sql.append(" AND a.diabetes_type <> '0'");
                        break;
                    case DiseaseTypeConstant.HYP:
                        sql.append(" AND a.hyp_type <> '0'");
                        break;
                }
            }
        }
        // 人群分类搜索
        if (StringUtils.isNotBlank(peopleType)) {
            switch (peopleType) {
                case PeopleTypeConstant.RISK:// 高危人群
                    sql.append(" AND a.is_risk <> '0'");
                    break;
                case PeopleTypeConstant.DISEASE:// 疾病人群
                    sql.append(" AND (a.apo_type <> '0' OR a.diabetes_type <> '0' OR a.hyp_type <> '0')");
                    break;
                case PeopleTypeConstant.HEALTHY:// 健康人群
                    sql.append(" AND a.apo_type = '0' AND a.diabetes_type = '0' AND a.hyp_type = '0'");
                    break;
            }
        }

        // ORDER BY
        sql.append(" ORDER BY CONVERT(a.name USING gbk) COLLATE gbk_chinese_ci");
        if (StringUtils.isNotEmpty(name)) {
            sql.append(", LOCATE('" + name + "', a.name), length(a.name)");
        }

        // LIMIT
        sql.append(" LIMIT " + (pageNo * (pageSize - 1)) + ", " + pageSize);
        List<DoctorTubeSignUser> rtnList = jdbcTemplate.query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<>(DoctorTubeSignUser.class));

        if (rtnList != null && rtnList.size() > 0) {
            List<SignDTO> dtoList = Lists.newArrayList();
            for (DoctorTubeSignUser doctorTubeSignUser : rtnList) {
                SignDTO signDTO = copyResidentInfo(doctorTubeSignUser);
                dtoList.add(signDTO);
            }
            return dtoList;
        }
        return null;
    }

    private SignDTO copyResidentInfo(DoctorTubeSignUser doctorTubeSignUser) {
        SignDTO dto = new SignDTO();
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
        // 是否签约
        if (StringUtils.isNotBlank(doctorTubeSignUser.getSignStatus())) {
            if (ResidentConstant.NORMAL.equals(doctorTubeSignUser.getSignStatus())) {
                dto.setIfSigned(true);
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
            String adr = String.format("%s%s%s",
                    //StringUtils.trimToEmpty(dictCache.queryArea(address.getProvince())),
                    //StringUtils.trimToEmpty(dictCache.queryArea(address.getCity())),
                    StringUtils.trimToEmpty(dictCache.queryArea(address.getCounty())),
                    StringUtils.trimToEmpty(dictCache.queryArea(address.getCommittee())),
                    StringUtils.trimToEmpty(address.getOther()));
            dto.setAddress(adr);
        }
        // 是否分组
        SignUserDoctorGroup signUserDoctorGroup = sudgRepo.queryFirst1ByDelFlagAndUid(CommonConstant.USED_DEL_FLAG, doctorTubeSignUser.getId());
        if (signUserDoctorGroup != null) {
            dto.setIfGrouped(true);
        } else {
            dto.setIfGrouped(false);
        }

        return dto;
    }
}

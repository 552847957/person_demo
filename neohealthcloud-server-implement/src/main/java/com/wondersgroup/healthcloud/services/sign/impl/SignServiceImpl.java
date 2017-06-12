package com.wondersgroup.healthcloud.services.sign.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.constant.CommonConstant;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.entity.group.SignUserDoctorGroup;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.jpa.repository.group.SignUserDoctorGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.AddressRepository;
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
    private DoctorTubeSignUserRepository dtsuRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SignUserDoctorGroupRepository sudgRepo;

    @Override
    public List<SignDTO> userLists(String name, int pageNo, int pageSize) {

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.* FROM fam_doctor_tube_sign_user a")
        .append(" LEFT JOIN (SELECT y.personcard FROM app_tb_sign_user_doctor_group x, app_tb_register_info y" +
                "            WHERE x.user_id = y.registerid AND y.personcard IS NOT NULL) b" +
                " ON a.card_number = b.personcard");

        if (StringUtils.isNotEmpty(name)) {
            sql.append(" WHERE LOCATE('" + name + "', a.name) > 0");
        }
        sql.append(" ORDER BY b.personcard DESC, CONVERT(a.name USING gbk) COLLATE gbk_chinese_ci");
        if (StringUtils.isNotEmpty(name)) {
            sql.append(", LOCATE('" + name + "', a.name), length(a.name)");
        }
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
        Address address = addressRepository.queryFirst1ByDelFlagAndUserId(CommonConstant.USED_DEL_FLAG, doctorTubeSignUser.getId());
        if (address != null) {
            String adr = String.format("%s%s%s", address.getProvince(), address.getCity(), address.getCounty());
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

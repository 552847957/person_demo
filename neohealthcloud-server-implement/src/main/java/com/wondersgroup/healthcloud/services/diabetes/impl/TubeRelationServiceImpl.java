package com.wondersgroup.healthcloud.services.diabetes.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.TubeRelation;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.TubeRelationRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.diabetes.TubeRelationService;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuchunliu on 2016/12/14.
 */
@Service("tubePatientService")
public class TubeRelationServiceImpl implements TubeRelationService {

    @Autowired
    private TubeRelationRepository relationRepo;

    @Autowired
    private DiabetesServiceImpl diabetesService;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    /**
     * 根据用户注册ID获取用户的在管信息
     *
     * @param registerid 用户主键
     * @param fresh      动态web端校验在管关系，默认true
     * @return
     */

    @Override
    public TubeRelation getTubeRelation(String registerid, Boolean fresh) {
        if (null == fresh || fresh) {
            relationRepo.deleteRelationByRegisterId(registerid);
            RegisterInfo registerInfo = registerInfoRepo.findOne(registerid);
            if (null == registerInfo || StringUtils.isEmpty(registerInfo.getPersoncard()) ||
                    StringUtils.isEmpty(registerInfo.getIdentifytype()) || "0".equals(registerInfo.getIdentifytype())) {
                return null;
            }
            TubePatientDetailDTO dto = diabetesService.getTubePatientDetail("01", registerInfo.getPersoncard());
            if (null == dto) {
                return null;
            }
            TubeRelation tubeRelation = new TubeRelation();
            tubeRelation.setId(IdGen.uuid());
            tubeRelation.setRegisterid(registerid);
            tubeRelation.setHospitalCode(dto.getHospitalCode());
            tubeRelation.setDoctorName(dto.getDoctorName());
            tubeRelation.setDelFlag("0");
            tubeRelation.setCreateDate(new Date());
            tubeRelation.setUpdateDate(new Date());
            tubeRelation = relationRepo.save(tubeRelation);
            return tubeRelation;
        } else {
            return relationRepo.getRelationByRegisterId(registerid);
        }
    }


    /**
     * 根据身份证号获取用户的在管信息
     *
     * @param personCard
     * @return
     */
    @Override
    public TubeRelation getTubeRelationByPersonCard(String personCard) {
        if (StringUtils.isEmpty(personCard)) {
            return null;
        }
        List<RegisterInfo> registerInfoList = registerInfoRepo.findByPersoncard(personCard);
        if (registerInfoList != null && registerInfoList.size() > 0) {
            String registerid = registerInfoList.get(0).getRegisterid();// 获取最后一次登录用户
            relationRepo.deleteRelationByRegisterId(registerid);
            TubePatientDetailDTO dto = diabetesService.getTubePatientDetail("01", personCard);
            if (null == dto) {
                return null;
            }
            TubeRelation tubeRelation = new TubeRelation();
            tubeRelation.setId(IdGen.uuid());
            tubeRelation.setRegisterid(registerid);
            tubeRelation.setHospitalCode(dto.getHospitalCode());
            tubeRelation.setDoctorName(dto.getDoctorName());
            tubeRelation.setDelFlag("0");
            tubeRelation.setCreateDate(new Date());
            tubeRelation.setUpdateDate(new Date());
            tubeRelation = relationRepo.save(tubeRelation);
            return tubeRelation;
        }
        return null;
    }
}

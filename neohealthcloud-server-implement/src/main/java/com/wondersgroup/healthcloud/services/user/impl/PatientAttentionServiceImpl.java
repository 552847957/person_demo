package com.wondersgroup.healthcloud.services.user.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.user.patientAttention.PatientAttention;
import com.wondersgroup.healthcloud.jpa.repository.user.doctor.PatientAttentionRepository;
import com.wondersgroup.healthcloud.services.user.PatientAttentionService;
import com.wondersgroup.healthcloud.services.user.exception.ErrorPatientAttentionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by longshasha on 16/8/17.
 */
@Service
public class PatientAttentionServiceImpl implements PatientAttentionService {

    @Autowired
    private PatientAttentionRepository patientAttentionRepository;

    @Override
    public Boolean doAttention(String uid, String doctorid) {
        PatientAttention check = patientAttentionRepository.findAttentionByUidAndDoctorId(uid, doctorid);
        if (check != null) {
            throw new ErrorPatientAttentionException(String.format("用户[%s]未关注用户[%s], 所以不能取消关注", uid, doctorid));
        }
        PatientAttention attention = new PatientAttention();
        attention.setId(IdGen.uuid());
        attention.setRegisterId(uid);
        attention.setAttentionId(doctorid);
        attention.setDelFlag("0");
        attention.setAttentionStarttime(new Date());
        attention.setCreateDate(new Date());
        attention.setUpdateDate(new Date());
        patientAttentionRepository.save(attention);
        return true;

    }

    @Override
    public Boolean delAttention(String uid, String doctorid) {
        PatientAttention attention = patientAttentionRepository.findAttentionByUidAndDoctorId(uid, doctorid);
        if (attention == null) {
            throw new ErrorPatientAttentionException(String.format("用户[%s]未关注用户[%s], 所以不能取消关注", uid, doctorid));
        }
        patientAttentionRepository.delete(attention);
        return true;
    }
}

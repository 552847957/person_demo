package com.wondersgroup.healthcloud.services.doctor.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInterventionRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorInterventionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

@Service
public class DoctorInterventionServiceImpl implements DoctorInterventionService {
    private static final Logger logger = LoggerFactory.getLogger("exlog");

    @Autowired
    private DoctorInterventionRepository doctorInterventionRepository;

    @Override
    public List<DoctorIntervention> list(DoctorIntervention doctorIntervention) {
        List<DoctorIntervention> rtnList = null;
        try {
            // 按照干预时间倒序排列
            rtnList = doctorInterventionRepository.findAll(Example.of(doctorIntervention), new Sort(Sort.Direction.DESC, "createTime"));
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return rtnList;
    }

    @Override
    public DoctorIntervention saveAndUpdate(DoctorIntervention doctorIntervention) {
        try {
            if(doctorIntervention.getId() == null) {
                doctorIntervention.setId(IdGen.uuid());
                doctorIntervention.setCreateTime(new Date());
                doctorIntervention.setDelFlag("0");
            }
            doctorIntervention.setUpdateTime(new Date());
            return doctorInterventionRepository.save(doctorIntervention);
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return null;
    }
}
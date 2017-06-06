package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorTemplate;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorTemplateRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DoctorTemplateServiceImpl implements DoctorTemplateService {

    private DoctorTemplateRepository doctorTemplateRepository;

    @Override
    public List<DoctorTemplate> findByDoctorIdAndType(String doctorId, String type) {
        return doctorTemplateRepository.findByDoctorIdAndType(doctorId, type);
    }

    @Override
    public DoctorTemplate update(String id, String doctorId, String type, String title, String content) {
        DoctorTemplate toSave;
        if (id == null) {
            if (doctorId == null) {
                throw new RuntimeException();
            }
            toSave = new DoctorTemplate();
            toSave.setId(IdGen.uuid());
            toSave.setDoctorId(doctorId);
        } else {
            toSave = doctorTemplateRepository.findOne(id);
        }
        toSave.setTitle(title);
        toSave.setContent(content);
        toSave.setType(type);
        toSave.setUpdateTime(new Date());

        return doctorTemplateRepository.save(toSave);
    }

    @Override
    public DoctorTemplate findOne(String id) {
        return doctorTemplateRepository.findOne(id);
    }

    @Override
    public void deleteOne(String id) {
        doctorTemplateRepository.delete(id);
    }

    @Override
    public void saveTemplate(DoctorTemplate entity) {
        if(StringUtils.isBlank(entity.getId())){
            entity.setId(IdGen.uuid());
        }
        doctorTemplateRepository.save(entity);
    }

    @Autowired
    public void setDoctorTemplateRepository(DoctorTemplateRepository doctorTemplateRepository) {
        this.doctorTemplateRepository = doctorTemplateRepository;
    }
}

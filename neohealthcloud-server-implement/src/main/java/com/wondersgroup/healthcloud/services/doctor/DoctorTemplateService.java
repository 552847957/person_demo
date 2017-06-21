package com.wondersgroup.healthcloud.services.doctor;


import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorTemplate;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorUsedTemplate;

import java.util.List;

public interface DoctorTemplateService {

    List<DoctorTemplate> findByDoctorIdAndType(String doctorId, String type);

    DoctorTemplate update(String id, String doctorId, String type, String title, String content);

    DoctorTemplate findOne(String id);

    void deleteOne(String id);

    void saveTemplate(DoctorTemplate entity);

    void saveDoctorUsedTemplate(DoctorUsedTemplate entity);

    List<DoctorTemplate> findLastUsedTemplate(String doctorId);

    Integer findDoctorTemplateCount(String doctorId);

    Integer findDefaultDoctorTemplateCount(String doctorId, String type);
}

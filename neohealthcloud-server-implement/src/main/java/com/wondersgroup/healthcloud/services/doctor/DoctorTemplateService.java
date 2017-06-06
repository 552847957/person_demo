package com.wondersgroup.healthcloud.services.doctor;


import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorTemplate;

import java.util.List;

public interface DoctorTemplateService {

    List<DoctorTemplate> findByDoctorIdAndType(String doctorId, String type);

    DoctorTemplate update(String id, String doctorId, String type, String title, String content);

    DoctorTemplate findOne(String id);

    void deleteOne(String id);

    void saveTemplate(DoctorTemplate entity);
}

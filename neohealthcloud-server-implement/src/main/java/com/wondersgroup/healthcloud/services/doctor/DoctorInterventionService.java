package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

public interface DoctorInterventionService {
    List<DoctorIntervention> list(DoctorIntervention doctorIntervention);
    DoctorIntervention saveAndUpdate(DoctorIntervention doctorIntervention);
}
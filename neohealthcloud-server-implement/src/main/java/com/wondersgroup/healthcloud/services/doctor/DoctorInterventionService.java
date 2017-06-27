package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

public interface DoctorInterventionService {
    List<DoctorIntervention> list(DoctorIntervention doctorIntervention);
    DoctorIntervention saveAndUpdate(DoctorIntervention doctorIntervention);

    NeoFamIntervention findLatestByInterventionId(String intervenId);

    List<NeoFamIntervention> findPatientBGOutlierListByIntervenId(String intervenId);

    List<NeoFamIntervention> findPatientPressureOutlierListByIntervenId(IntervenEntity interven);
}
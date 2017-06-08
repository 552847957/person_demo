package com.wondersgroup.healthcloud.services.interven;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;

import java.util.List;

/**
 * Created by longshasha on 17/5/18.
 * 医生端4.1 医生干预service
 */
public interface DoctorIntervenService {

    List<IntervenEntity> findTodoInterveneList(String name,String uid, String sign, String interven_type, int pageNo, int pageSize);

    List<NeoFamIntervention> findBloodGlucoseOutlierListByRegisterId(String registerId, Boolean is_all, int pageNo, int pageSize, int size);

    List<NeoFamIntervention> findpressureOutlierListByRegisterId(String registerId, Boolean is_all, int pageNo, int pageSize, int size);

    String findNotDealInterveneTypes(String registerId);

    void intervenSaveOrUpdate(String doctorId, String patientId, String content);

    List<IntervenEntity> findPersonalInterveneList(String uid, int pageNo, int pageSize);

    List<NeoFamIntervention> findBloodGlucoseOutlierListByInterventionId(String interventionId, Boolean is_all, int pageNo, int pageSize, int size);

    List<NeoFamIntervention> findpressureOutlierListByInterventionId(String interventionId, Boolean is_all, int pageNo, int pageSize, int size);

    int countHasInterventionByDoctorId(String doctorId);

    IntervenEntity getUserDiseaseLabelByRegisterId(String registerId);

    Boolean hasTodoIntervensByRegisterId(String registerId);

}

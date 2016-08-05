package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;

import java.util.Map;

/**
 * Created by longshasha on 16/8/1.
 */
public interface DoctorService {

    Map<String,Object> findDoctorInfoByUid(String id);

    Map<String,Object> findDoctorInfoByActcode(String actcode);

    DoctorInfo updateExpertin(String uid, String expertin);

    DoctorAccount updateDoctorAvatar(String uid, String avatar);

    DoctorInfo updateIntro(String uid, String intro);
}

package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/1.
 */
public interface DoctorService {

    Map<String,Object> findDoctorInfoByUidAndDoctorId(String uid,String doctorId);

    Map<String,Object> findDoctorInfoByUid(String uid);

    Map<String,Object> findDoctorInfoByActcode(String actcode);

    DoctorInfo updateExpertin(String uid, String expertin);

    DoctorAccount updateDoctorAvatar(String uid, String avatar);

    DoctorInfo updateIntro(String uid, String intro);

    List<Map<String,Object>> findAllFaqDoctors(String kw);
}

package com.wondersgroup.healthcloud.services.doctor;

import java.util.Map;

/**
 * Created by longshasha on 16/8/1.
 */
public interface DoctorService {

    Map<String,Object> findDoctorInfoByUid(String id);

    Map<String,Object> findDoctorInfoByActcode(String actcode);
}

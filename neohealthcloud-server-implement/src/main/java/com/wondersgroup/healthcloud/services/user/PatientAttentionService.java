package com.wondersgroup.healthcloud.services.user;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/17.
 */
public interface PatientAttentionService {
    Boolean doAttention(String uid, String doctorid);

    Boolean delAttention(String uid, String doctorid);

    List<Map<String,Object>> findAttentionDoctorList(String uid, int pageSize, Integer flag);
}

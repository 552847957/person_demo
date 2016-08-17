package com.wondersgroup.healthcloud.services.user;

/**
 * Created by longshasha on 16/8/17.
 */
public interface PatientAttentionService {
    Boolean doAttention(String uid, String doctorid);

    Boolean delAttention(String uid, String doctorid);
}

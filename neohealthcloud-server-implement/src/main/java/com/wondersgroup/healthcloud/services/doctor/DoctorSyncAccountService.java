package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;

/**
 * Created by longshasha on 16/8/2.
 */
public interface DoctorSyncAccountService {

    DoctorAccount findDoctorByMobileWithOutDelfag(String mobile);

    DoctorAccount openWonderCloudAccount(DoctorAccount doctorAccount, DoctorInfo doctorInfo,String roles);

    void closeWonderCloudAccount(String register_id);

    DoctorInfo findDoctorByPersoncardWithOutDelflag(String idcard);

    DoctorAccount findDoctorById(String id);
}

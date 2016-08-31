package com.wondersgroup.healthcloud.helper.push.area;

import com.wondersgroup.healthcloud.helper.push.getui.PushClient;
import com.wondersgroup.healthcloud.jpa.entity.app.UserPushInfo;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/15/16.
 */
public class PushAreaService {

    private UserPushInfoRepository userPushInfoRepository;
    private DoctorAccountRepository doctorAccountRepository;
    private PushClientSelector pushClientSelector;


    public PushClient getByUser(String uid) {
        List<UserPushInfo> list = userPushInfoRepository.findByUid(uid);
        if (list.size() == 0) {
            return null;
        } else {
            return pushClientSelector.getByArea(list.get(0).getArea(), false);
        }
    }

    public PushClient getByDoctor(String doctorId) {
        DoctorAccount doctorAccount = doctorAccountRepository.findOne(doctorId);
        if (doctorAccount == null) {
            return null;
        } else {
            return null;
//            return pushClientSelector.getByArea("", true);//todo
        }
    }

    public PushClient getByArea(String area, Boolean isDoctor) {
        return pushClientSelector.getByArea(area, isDoctor);
    }

    @Autowired
    public void setUserPushInfoRepository(UserPushInfoRepository userPushInfoRepository) {
        this.userPushInfoRepository = userPushInfoRepository;
    }

    @Autowired
    public void setDoctorAccountRepository(DoctorAccountRepository doctorAccountRepository) {
        this.doctorAccountRepository = doctorAccountRepository;
    }

    @Autowired
    public void setPushClientSelector(PushClientSelector pushClientSelector) {
        this.pushClientSelector = pushClientSelector;
    }
}

package com.wondersgroup.healthcloud.services.doctormessage;


import java.util.List;

import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorPushMessage;

/**
 * Created by qiujun on 2015/9/10.
 */
public interface ManageDoctorMessageService {

    public boolean addDoctorMessage(DoctorMessage message);

    public boolean batchAddDoctorMessages(List<DoctorMessage> messages);

    public List<DoctorMessage> queryDoctorMessageByTpye(String type, String receiveId);

    public boolean deleteDoctorPushMessage(List<String> pushMessages);

    public boolean addDoctorPushMessage(DoctorPushMessage pushMessage);

    public List<DoctorPushMessage> getAllPushMessages();
}

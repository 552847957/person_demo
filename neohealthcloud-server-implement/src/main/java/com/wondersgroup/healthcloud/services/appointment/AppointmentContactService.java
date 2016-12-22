package com.wondersgroup.healthcloud.services.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentContact;

import java.util.List;

/**
 * Created by longshasha on 16/12/8.
 */
public interface AppointmentContactService {

    AppointmentContact addAppointmentContact(String uid, String name, String idcard, String mobile, String mediCardId);

    List<AppointmentContact> getAppointmentContactList(String uid);

    AppointmentContact getAppointmentContactById(String id);

    AppointmentContact getDefaultAppointmentContactByUid(String uid);

    Integer updateIsDefaultContact(String uid, String id);
}

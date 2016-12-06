package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by longshasha on 16/3/7.
 */
public interface ContactRepository extends JpaRepository<AppointmentContact, String> {

    @Query("select a from AppointmentContact a where a.uid = ?1 and a.delFlag = '0' order by a.isDefault desc,a.createTime asc")
    List<AppointmentContact> getAppointmentContactListByUid(String uid);

    @Modifying
    @Query("update AppointmentContact a set a.isDefault='0' where a.uid=?1 and  a.id<>?2 and a.delFlag='0' ")
    Integer updateIsDefaultContactByuid(String uid, String id);

    @Query("select a from AppointmentContact a where a.uid = ?1 and a.idcard = ?2 and a.delFlag ='0' ")
    AppointmentContact getAppointmentContactByIdCard(String uid, String idcard);

    @Modifying
    @Query("update AppointmentContact a set a.isDefault='1' where a.id=?1 and a.delFlag='0' ")
    Integer updateIsDefaultContactByid(String id);

    @Modifying
    @Query("update AppointmentContact a set a.delFlag='1' where a.id=?1 and a.delFlag='0'  ")
    Integer deleteAppointmentContactById(String id);

    @Query("select a from AppointmentContact a where a.id = ?1 and a.delFlag ='0' ")
    AppointmentContact getAppointmentContactById(String id);
}

package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentSmsTemplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/12/23.
 */
public interface SmsTempletRepository extends JpaRepository<AppointmentSmsTemplet, String> {


    @Query("select a from AppointmentSmsTemplet a where a.hospitalCode in ?1 ")
    List<AppointmentSmsTemplet> findSmsTempletsByHospitalCodes(List<String> hospitalCodes);

}

package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentSmsTemplet;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by longshasha on 16/12/23.
 */
public interface SmsTempletRepository extends JpaRepository<AppointmentSmsTemplet, String> {


}

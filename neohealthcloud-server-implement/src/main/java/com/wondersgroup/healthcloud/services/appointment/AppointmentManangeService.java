package com.wondersgroup.healthcloud.services.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;

import java.util.List;

/**
 * Created by longshasha on 16/12/12.
 */
public interface AppointmentManangeService {

    List<AppointmentHospital> findAllManageHospitalListByAreaCodeAndName(String name, String areaCode, int pageNum, int size);

    int countHospitalsByAreaCode(String name, String areaCode);

    List<OrderDto> findAllManageOrderListByNameAndMobile(String patientName, String patientMobile,String id, int pageNum, int size,Boolean isList);

    int countOrdersByNameAndMobile(String patientName, String patientMobile);

    void batchSetIsonsaleByHospitalIds(List<String> hospitalIds, String isonsale);
}

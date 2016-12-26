package com.wondersgroup.healthcloud.services.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/12.
 */
public interface AppointmentManangeService {

    List<AppointmentHospital> findAllManageHospitalListByAreaCodeAndName(String name, String areaCode, int pageNum, int size,Boolean isPage);

    int countHospitalsByAreaCode(String name, String areaCode);

    List<OrderDto> findAllManageOrderListByNameAndMobile(String patientName, String patientMobile,String id, int pageNum, int size,Boolean isList);

    int countOrdersByNameAndMobile(String patientName, String patientMobile);

    void batchSetIsonsaleByHospitalIds(List<String> hospitalIds, String isonsale);

    List<AppointmentL1Department> findManageAppointmentL1Department(String hospital_id);

    List<AppointmentL2Department> findManageAppointmentL2Department(String department_l1_id);

    List<AppointmentDoctor> findAllManageDoctorListByMap(Map<String,Object> parameter, int pageNum, int size);

    int countDoctorByMap(Map parameter);

    AppointmentSmsTemplet findSmsTempletByHosCode(String hosOrgCode);
}

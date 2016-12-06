package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.appointment.AppointmentDoctorDTO;
import com.wondersgroup.healthcloud.api.http.dto.appointment.AppointmentHospitalDTO;
import com.wondersgroup.healthcloud.api.http.dto.appointment.AreaDTO;
import com.wondersgroup.healthcloud.api.http.dto.appointment.SearchDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctor;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.utils.EmojiUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/5.
 */
@RestController
@RequestMapping("/api/reservation")
public class AppointmentResourceController {

    @Autowired
    private AppointmentApiService appointmentApiService;


    /**
     * 查询预约挂号地址列表
     * @param request
     * @return
     */
    @VersionRange
    @RequestMapping(value="/areas",method = RequestMethod.GET)
    public JsonListResponseEntity<AreaDTO> getAreasDict(HttpServletRequest request){
        JsonListResponseEntity<AreaDTO> areaJsonListResponseEntity = new JsonListResponseEntity<>();
        List<AreaDTO> list = new ArrayList<AreaDTO>();
        AreaDTO area = new AreaDTO();
        area.setAreaCode("310100000000");
        area.setAreaName("全上海");
        area.setAreaUpperCode("310000000000");
        list.add(area);
        List<Map<String,Object>> resultList =  appointmentApiService.findAppointmentAreaByUpperCode(area.getAreaCode());
        for(Map<String,Object> result:resultList){
            list.add(new AreaDTO(result));
        }
        areaJsonListResponseEntity.setContent(list);
        return areaJsonListResponseEntity;
    }


    /**
     * 根据地区查询医院列表
     *
     * @param areaCode
     * @param kw
     *
     * 如果kw不为空为 搜索查询更多
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/hospital/list", method = RequestMethod.GET)
    public JsonListResponseEntity getHospitalList(
            @RequestParam(required = false, defaultValue = "",value = "area_code" ) String areaCode,
            @RequestParam(required = false, defaultValue = "1") Integer flag,
            @RequestParam(required = false, defaultValue = "") String kw) {

        JsonListResponseEntity<AppointmentHospitalDTO> body = new JsonListResponseEntity<>();
        List<AppointmentHospitalDTO> list = Lists.newArrayList();

        int pageSize = 10;
        Boolean more = false;
        List<AppointmentHospital> appointmentHospitals = appointmentApiService.findAllHospitalListByAreaCodeOrKw(kw, areaCode, flag, pageSize);

        if(appointmentHospitals.size()>10){
            more = true;
            flag = flag + 1;
        }
        AppointmentHospitalDTO hospitalDTO;
        for (AppointmentHospital hospital : appointmentHospitals) {
            hospitalDTO = new AppointmentHospitalDTO(hospital);
            hospitalDTO.setDoctorNum(appointmentApiService.countDoctorNumByHospitalId(hospital.getId()));
            list.add(hospitalDTO);
        }

        body.setContent(list, more, null, String.valueOf(flag));
        return body;

    }

    /**
     * 搜索
     *
     * @param kw
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public JsonResponseEntity search(
            @RequestParam(required = false, defaultValue = "") String kw) {

        JsonResponseEntity<SearchDTO> body = new JsonResponseEntity<>();

        SearchDTO searchDTO = new SearchDTO();

        if(StringUtils.isNotBlank(kw)){
            String cleanName = EmojiUtils.cleanEmoji(kw);
            if(kw.length() > cleanName.length()){
                return body;
            }
        }

        /**
         * 医生
         */
        List<AppointmentDoctorDTO> doctorDTOList = Lists.newArrayList();
        Boolean moreDoctor = false;
        int pageSize = 10;
        int pageNum = 1;

        List<AppointmentDoctor> appointmentDoctors = appointmentApiService.findDoctorListByKw(kw,pageSize,pageNum);
        if(appointmentDoctors.size()>2){
            moreDoctor = true;
        }
        AppointmentDoctorDTO doctorDTO = new AppointmentDoctorDTO();
        int count = 1;
        for (AppointmentDoctor doctor : appointmentDoctors) {
            if(count<3){
                doctorDTO = doctorDTO.getDoctorDTOList(doctor);
                doctorDTOList.add(doctorDTO);
                count+=1;
            }

        }

        SearchDTO.SearchDoctorDTO searchDoctorDTO = new SearchDTO().new SearchDoctorDTO(doctorDTOList,moreDoctor);


        /**
         * 医院
         */
        List<AppointmentHospitalDTO> hospitalDTOList = Lists.newArrayList();
        Boolean moreHospital = false;

        List<AppointmentHospital> appointmentHospitals = appointmentApiService.findAllHospitalListByKw(kw,pageSize,pageNum);
        if(appointmentHospitals.size()>2){
            moreHospital = true;
        }
        AppointmentHospitalDTO hospitalDTO;
        int c = 1;
        for (AppointmentHospital hospital : appointmentHospitals) {
            if(c<3){
                hospitalDTO = new AppointmentHospitalDTO(hospital);
                hospitalDTO.setDoctorNum(appointmentApiService.countDoctorNumByHospitalId(hospital.getId()));
                hospitalDTOList.add(hospitalDTO);
                c+=1;
            }

        }

        SearchDTO.SearchHospitalDTO searchHospitalDTO = new SearchDTO().new SearchHospitalDTO(hospitalDTOList,moreHospital);


        searchDTO.setDoctor(searchDoctorDTO);
        searchDTO.setHospital(searchHospitalDTO);
        body.setData(searchDTO);
        return body;

    }


    /**
     * 搜索
     *
     * @param kw 搜索词
     * @param flag 分页
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/searchMore/doctor", method = RequestMethod.GET)
    public JsonListResponseEntity searchMoreDoctors(
            @RequestParam(required = false, defaultValue = "1") Integer flag,
            @RequestParam(required = false, defaultValue = "") String kw){

        JsonListResponseEntity<AppointmentDoctorDTO> body = new JsonListResponseEntity<>();
        List<AppointmentDoctorDTO> doctorDTOList = Lists.newArrayList();
        Boolean moreDoctor = false;
        int pageSize = 10;

        List<AppointmentDoctor> appointmentDoctors = appointmentApiService.findDoctorListByKw(kw,pageSize,flag);
        if(appointmentDoctors.size()>pageSize){
            moreDoctor = true;
        }
        AppointmentDoctorDTO doctorDTO = new AppointmentDoctorDTO();
        for (AppointmentDoctor doctor : appointmentDoctors) {
            doctorDTO = doctorDTO.getDoctorDTOList(doctor);
                doctorDTOList.add(doctorDTO);
        }

        body.setContent(doctorDTOList, moreDoctor, null, String.valueOf(flag));
        return body;

    }

    /**
     * 根据医院Id查询医院详情(医院主页)
     * @param hospitalId
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/hospital/detail", method = RequestMethod.GET)
    public JsonResponseEntity hospitalDetail(
            @RequestParam(required = true, defaultValue = "",value = "id" ) String hospitalId) {
        JsonResponseEntity<AppointmentHospitalDTO> body = new JsonResponseEntity<>();

        AppointmentHospital hospital = appointmentApiService.findHospitalById(hospitalId);
        AppointmentHospitalDTO hospitalDTO = AppointmentHospitalDTO.getHospitalDetail(hospital);
        body.setData(hospitalDTO);
        return body;
    }











}

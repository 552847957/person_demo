package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceEntity;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by shenbin on 16/8/5.
 */

@RestController
@RequestMapping(value = "/api")
public class DoctorInfoController {

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorServiceRepository doctorServiceRepository;

    /**
     * 保存医生信息
     * @param
     * @return
     */
    @PostMapping(path = "/doctorInfo/save")
    public JsonResponseEntity saveDoctorInfo(@RequestBody Doctor doctor){

        if(doctor!=null && StringUtils.isNotBlank(doctor.getUid())){
            DoctorInfo doctorInfo = doctorInfoRepository.findOne(doctor.getUid());
            if(doctorInfo!=null){
                doctorInfo.setExpertin(doctor.getExpertin());
                doctorInfo.setIntroduction(doctor.getIntroduction());
                doctorInfo.setDepartStandard(doctor.getDepartStandard());
                doctorInfoRepository.saveAndFlush(doctorInfo);
            }
            DoctorAccount doctorAccount = doctorAccountRepository.findOne(doctor.getUid());
            if(doctorAccount!=null){
                doctorAccount.setIsAvailable(doctor.getIsAvailable());
                doctorAccountRepository.saveAndFlush(doctorAccount);
            }

            //保存服务
            if("0".equals(doctor.getIsAvailable())){
                doctorServiceRepository.removeServiceByUid(doctor.getUid());
                List<DoctorServiceEntity> doctorServiceEntityList = new ArrayList<>();
                List<String> serviceIds = doctor.getServiceIds();
                iteratorDoctorService(serviceIds, doctor.getUid(), doctorServiceEntityList);
                doctorServiceRepository.save(doctorServiceEntityList);
            }

            return new JsonResponseEntity(0, "保存成功");
        }else{
            return new JsonResponseEntity(3101, "保存失败,参数有误");
        }


    }

    /**
     * 查询医生信息
     * @return
     */
    @GetMapping(path = "/doctorInfo/find")
    public JsonResponseEntity<Doctor> findDoctorInfo(@RequestParam String uid) throws JsonProcessingException {

        JsonResponseEntity<Doctor> response = new JsonResponseEntity<>();

        Doctor doctor = doctorService.findDoctorByUid(uid);

        List<String> serviceIds = Lists.newArrayList();
        List<Map<String, Object>> serviceMaps = doctorService.findDoctorServicesByIdWithoutDel(uid);
        if(serviceMaps!=null && serviceMaps.size()>0){
            for(Map<String,Object> map : serviceMaps){
                serviceIds.add(map.get("id").toString());
            }
        }
        doctor.setServiceIds(serviceIds);

        if(doctor==null){
            response.setCode(3101);
            response.setMsg("不存在的医生");
            return response;
        }
        response.setData(doctor);
        return response;
    }

    /**
     * 保存医生服务
     * @return
     */
    @RequestMapping(value = "/doctorService/save", method = RequestMethod.POST)
    public JsonResponseEntity saveDoctorService(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String doctorId = reader.readString("doctorId", true);
        List<String> serviceId = reader.readObject("serviceId", true,List.class);
        DoctorInfo doctor = doctorInfoRepository.findById(doctorId);

        if (doctor != null) {
            doctorServiceRepository.removeServiceByUid(doctorId);
            List<DoctorServiceEntity> doctorServiceEntityList = new ArrayList<>();
            iteratorDoctorService(serviceId, doctorId, doctorServiceEntityList);
            doctorServiceRepository.save(doctorServiceEntityList);
            return new JsonResponseEntity(0, "保存成功");
        }
        throw new ErrorDoctorAccountNoneException();
    }

    /**
     * 批量保存医生服务
     * @return
     */
    @RequestMapping(value = "/doctorService/batchSave", method = RequestMethod.POST)
    public JsonResponseEntity batchSaveDoctorService(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        List<String> doctorId = reader.readObject("doctorId", true,List.class);
        List<String> serviceId = reader.readObject("serviceId", true,List.class);
        for (String id : doctorId) {
            DoctorInfo doctor = doctorInfoRepository.findById(id);

            if (doctor != null) {
                doctorServiceRepository.removeServiceByUid(id);
                List<DoctorServiceEntity> doctorServiceEntityList = new ArrayList<>();
                iteratorDoctorService(serviceId, id, doctorServiceEntityList);
                doctorServiceRepository.save(doctorServiceEntityList);
            } else {
                throw new ErrorDoctorAccountNoneException();
            }
        }
        return new JsonResponseEntity(0, "保存成功");
    }


    private void iteratorDoctorService(List<String> serviceId,
                                       String doctorId,
                                       List<DoctorServiceEntity> doctorServiceEntityList) {
        for (String service : serviceId) {
            DoctorServiceEntity doctorServiceEntity = new DoctorServiceEntity();
            doctorServiceEntity.setId(IdGen.uuid());
            doctorServiceEntity.setDoctorId(doctorId);
            doctorServiceEntity.setServiceId(service);
            doctorServiceEntity.setCreateDate(new Date());
            doctorServiceEntity.setUpdateDate(new Date());
            doctorServiceEntityList.add(doctorServiceEntity);
        }
    }


    /**
     * 用于H5页面 -- 医生详情
     * @param doctorId
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(path = "/doctor/detail")
    public JsonResponseEntity<Doctor> detail(@RequestParam String doctorId) throws JsonProcessingException {

        JsonResponseEntity<Doctor> response = new JsonResponseEntity<>();

        Doctor doctor = doctorService.findDoctorByUid(doctorId);

        if(doctor==null){
            response.setCode(3101);
            response.setMsg("不存在的医生");
            return response;
        }
        response.setData(doctor);
        return response;
    }

}

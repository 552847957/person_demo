package com.wondersgroup.healthcloud.services.doctor.impl;

import com.google.common.collect.Lists;
import com.qiniu.util.StringUtils;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartmentRela;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorDepartmentRelaRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorConcerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
@Service
public class DoctorConcerServiceImpl implements DoctorConcerService {

    @Autowired
    private DoctorDepartmentRelaRepository doctorDepartmentRelaRepository;



    @Override
    @Transactional
    public Boolean updateDoctorConcerDepartment(String doctorId, String departmentIds) {
        if(!StringUtils.isNullOrEmpty(departmentIds)&&!StringUtils.isNullOrEmpty(doctorId)){
            doctorDepartmentRelaRepository.deleteById(doctorId);//先删除医生和科室的关系,在维护新的关系
            String[] ids = departmentIds.split(",");
            List<DoctorDepartmentRela> relations = Lists.newArrayList();
            for(String id:ids){
                DoctorDepartmentRela rela = new DoctorDepartmentRela();
                rela.setId(IdGen.uuid());
                rela.setDoctorid(doctorId);
                rela.setDepartid(id);
                rela.setCreateBy(doctorId);
                rela.setCreateDate(new Date());
                rela.setUpdateBy(doctorId);
                rela.setDelFlag("0");
                rela.setUpdateDate(new Date());
                relations.add(rela);
            }
            doctorDepartmentRelaRepository.save(relations);
            return true;
        }
        return false;
    }

    @Override
    public List<DepartGB> queryDoctorDepartmentsByDoctorId(String doctorId) {
        return doctorDepartmentRelaRepository.queryDoctorDepartmentsByDoctorId(doctorId);
    }




    private List<String> convert(List<Object> result){
        List<String> ids = Lists.newArrayList();
        if(result!=null&&!result.isEmpty()){
            for(Object obj: result){
                ids.add((String)obj);
            }
        }
        return ids;
    }
}
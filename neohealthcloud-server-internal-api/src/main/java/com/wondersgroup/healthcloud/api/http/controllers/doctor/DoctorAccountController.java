package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by shenbin on 16/8/5.
 */
@RestController
@RequestMapping(value = "/api")
public class DoctorAccountController {

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;

    @Autowired
    private DoctorService doctorService;

    @RequestMapping(value = "/doctor/list", method = RequestMethod.POST)
    public Pager findDoctorList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        List<Doctor> doctorList = doctorService.findDoctorListByPager(pageNum,pager.getSize(),pager.getParameter());

        int totalSize = doctorService.countFaqByParameter(pager.getParameter());
        pager.setTotalElements(totalSize);
        pager.setData(doctorList);
        return pager;
    }

    /**
     * 批量修改医生账号可用状态
     * @return
     */
    @PostMapping(path = "/doctor/available")
    public JsonResponseEntity updateIsAvailable(@RequestBody String body){
        JsonKeyReader reader = new JsonKeyReader(body);
        String ids = reader.readString("ids", true);
        String isAvailable = reader.readString("isAvailable", true);
        if(StringUtils.isNotBlank(ids)){
            String[] idArray = ids.split(",");
            List<String> idsList = Lists.newArrayList();

            for(String str : idArray){
                idsList.add(str);
            }
            doctorAccountRepository.updateIsAvailable(isAvailable, idsList);
        }

        return new JsonResponseEntity(0, "修改成功");
    }

}

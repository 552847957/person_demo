package com.wondersgroup.healthcloud.api.http.controllers.disease;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.disease.FollowRemindDto;
import com.wondersgroup.healthcloud.api.http.dto.doctor.disease.FollowRemindMineDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRemindRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.UserInfoRepository;
import com.wondersgroup.healthcloud.services.disease.FollowRemindService;
import com.wondersgroup.healthcloud.services.disease.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 随访提醒
 * Created by zhuchunliu on 2017/5/23.
 */
@RestController
@RequestMapping(value = "/api/follow")
public class FollowRemindController {

    @Autowired
    private DoctorInfoRepository doctorInfoRepo;

    @Autowired
    private DoctorAccountRepository doctorAccountRepo;

    @Autowired
    private FollowRemindService followRemindService;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    @Autowired
    private UserInfoRepository userInfoRepo;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private DiabetesAssessmentRemindRepository remindRepo;

    /**
     * 随访提醒列表
     * @param doctorId 医生主键
     * @param signStatus 签约状态 1：已经签约居民，0：未签约居民，null：所有类型的居民
     * @param diseaseType 慢病类型 1：糖尿病，2：高血压，3：脑卒中，null：所有类型的居民，逗号间隔
     * @return
     */
    @GetMapping("/list")
    public JsonListResponseEntity list(
            @RequestParam(required = true) String doctorId,
            @RequestParam(required = false) Integer  signStatus,
            @RequestParam(required = false) String  diseaseType,
            @RequestParam(required = false, defaultValue = "1") Integer flag) {

        int pageSize = 20;
        JsonListResponseEntity response = new JsonListResponseEntity();

        DoctorInfo doctorInfo = doctorInfoRepo.findOne(doctorId);
        DoctorAccount doctorAccount = doctorAccountRepo.findOne(doctorId);
        if(null == doctorInfo){
            response.setCode(1001);
            response.setMsg("不存在当前医生信息");
            return response;
        }

        List<Map<String,Object>> list = followRemindService.findFollow(flag,pageSize,signStatus,diseaseType,doctorInfo,doctorAccount);
        boolean hasMore = false;
        if(list.size() > pageSize){
            hasMore = true;
            flag++;
            list.remove(10);
        }

        List<FollowRemindDto> entityList = Lists.newArrayList();
        for(Map<String,Object> map : list)
            entityList.add(new FollowRemindDto(map, registerInfoRepo.findOne(map.get("registerid").toString()),
                    userInfoRepo.findOne(map.get("registerid").toString())));

        response.setContent(entityList,hasMore,null,flag.toString());
        return response;
    }

    /**
     * 我的随访列表
     * @param doctorId 医生主键
     * @return
     */
    @GetMapping("/mine")
    public JsonListResponseEntity mine(
            @RequestParam(required = true) String doctorId,
            @RequestParam(required = false, defaultValue = "1") Integer flag) {

        int pageSize = 20;
        JsonListResponseEntity response = new JsonListResponseEntity();

        DoctorInfo doctorInfo = doctorInfoRepo.findOne(doctorId);
        DoctorAccount doctorAccount = doctorAccountRepo.findOne(doctorId);
        if(null == doctorInfo){
            response.setCode(1001);
            response.setMsg("不存在当前医生信息");
            return response;
        }

        List<Map<String,Object>> list = followRemindService.findMineFollow(flag,pageSize,doctorInfo,doctorAccount);
        boolean hasMore = false;
        if(list.size() > pageSize){
            hasMore = true;
            flag++;
            list.remove(pageSize);
        }

        List<FollowRemindMineDto> entityList = Lists.newArrayList();
        for(Map<String,Object> map : list)
            entityList.add(new FollowRemindMineDto(map, registerInfoRepo.findOne(map.get("registerid").toString()),
                    userInfoRepo.findOne(map.get("registerid").toString())));

        response.setContent(entityList,hasMore,null,flag.toString());
        return response;
    }

    /**
     * 高危提醒
     * @return
     */
    @PostMapping("/remind")
    public JsonResponseEntity remind(@RequestBody String request) {

        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String ids = reader.readString("ids",false);
        String doctorId = reader.readString("doctorId",false);

        List<String> registerIds = remindRepo.findFollowByRegisterId(ids.split(","),2);
        if(0 == registerIds.size()){
            entity.setCode(1001);
            entity.setMsg("该用户当日已经被提醒");
            return entity;
        }

        Boolean flag = screeningService.remind(registerIds,doctorId,2);

        if(flag){
            entity.setMsg("您的随访提醒已经发送成功");
        }else{
            entity.setCode(1002);
            entity.setMsg("您的随访提醒发送失败");
        }
        return entity;
    }


}

package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.services.disease.FollowRemindService;
import com.wondersgroup.healthcloud.services.disease.ScreeningService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.group.PatientGroupService;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.question.DoctorQuestionService;
import com.wondersgroup.healthcloud.services.sign.SignService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/28.
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorQuestionService doctorQuestionService;

    @Autowired
    private DoctorIntervenService doctorIntervenService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private FollowRemindService followRemindService;

    @Autowired
    private PatientGroupService patientGroupService;

    @Autowired
    private DoctorTubeSignUserRepository doctorTubeSignUserRepository;

    @Autowired
    private JedisPool jedisPool;

    private static final Logger log = Logger.getLogger(HomeController.class);



    @RequestMapping(value = "/doctorServices", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity doctorServices(@RequestParam(required = true) String uid){
        JsonResponseEntity body = new JsonResponseEntity();

        List<Map<String,Object>> services = doctorService.findDoctorServicesById(uid);
        if(services.size()>0){
            for(Map<String,Object> service :services){
                if(service.containsKey("keyword") && service.get("keyword")!=null && service.get("keyword").equals("Q&A")){
                    int unread = 0;
                    try {
                         unread = doctorQuestionService.queryUnreadCount(uid);// 杜宽心的接口
                    }catch (Exception e){
                        log.error("HomeController-error:"+e.getLocalizedMessage());
                    }
                    service.put("unread",unread);
                }
            }
        }

        body.setData(services);

        return body;
    }

    /**
     * 首页汇总数据
     * @param uid
     * @return
     */
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity statistics(@RequestParam(required = true) String uid){
        JsonResponseEntity body = new JsonResponseEntity();

        DoctorInfo doctorInfo = doctorService.getDoctorInfoByUid(uid);
        DoctorAccount doctorAccount = doctorService.getDoctorAccountByUid(uid);
        if(null == doctorInfo){
            body.setCode(1001);
            body.setMsg("不存在当前医生信息");
            return body;
        }

        Map<String,Object>  number = new HashMap<>();
        int answeredNum = 0;
        int intervenNum = 0;
        int screenNum = 0;
        int followNum = 0;
        int groupNum = 0;
        int signedNum = 0;
        Boolean ask_red = false;
        Boolean follow_red = false;
        Boolean screened_red = false;
        Boolean intervened_red = false;

        try {
            signedNum = doctorTubeSignUserRepository.countSignedUserByDoctorId(uid);
            answeredNum = doctorQuestionService.queryAnsweredCount(uid);
            intervenNum = doctorIntervenService.countHasInterventionByDoctorId(uid);//已干预数量
            screenNum = screeningService.getRemindCount(uid);
            followNum = followRemindService.getRemindCount(uid);
            groupNum = patientGroupService.getGroupNumByDoctorId(uid);
            int unRead = doctorQuestionService.queryUnreadCount(uid);
            if(unRead>0)
                ask_red = true;
            intervened_red = doctorIntervenService.hasTodoIntervensByDoctorId(uid);
            if(groupNum==0){
                groupNum=1;//初始给一个默认分组
            }
            follow_red = followRemindService.hasToRemindFollow(doctorInfo,doctorAccount);
            screened_red = screeningService.hasToRemindScreened(doctorInfo);
        }catch (Exception e){
            log.error("HomeController-error:doctorQuestionService.queryUnreadCount:"+e.getLocalizedMessage());
        }
        number.put("signed_num",getNumStr(signedNum));//我的签约-签约人数
        number.put("group_num",getNumStr(groupNum));//我的分组-分组数
        number.put("screened_num",getNumStr(screenNum));//筛查提醒-已筛查数
        number.put("intervened_num",getNumStr(intervenNum));//异常干预-已干预人数
        number.put("follow_num",getNumStr(followNum));//随访提醒-已随访人数
        number.put("ask_answered",getNumStr(answeredNum));//问诊回答—已回答数

        number.put("ask_red",ask_red);//问诊回答-红点
        number.put("follow_red",follow_red);//随访提醒-红点
        number.put("screened_red",screened_red);//筛查提醒-红点
        number.put("intervened_red",intervened_red);//异常干预-红点

        body.setData(number);

        return body;
    }

/*
    /**
     * 将数据放入缓存,与现在的数据比较判断是否有红点
     * @param jedis
     * @param type
     * @param uid
     * @param value
     * @return
     *//*
    private Boolean getIsRedByRedis(Jedis jedis,String type, String uid,int value) {

        Boolean isRed = false;
        String value_cache_key = String.format("health-doctor-%s-%s",type, uid);
        if (!jedis.exists(value_cache_key)) {
            if(value>0)
                isRed = true;
        }else{
            int value_cache = Integer.valueOf(jedis.get(value_cache_key));
            if(value_cache<value){
                isRed = true;
            }
        }
        jedis.set(value_cache_key,String.valueOf(value));
        return isRed;
    }*/

    public String getNumStr(int number){
        if(number<=999){
            return String.valueOf(number);
        }else{
            return "999+";
        }
    }

}

package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.disease.FollowRemindService;
import com.wondersgroup.healthcloud.services.disease.ScreeningService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.question.DoctorQuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                        log.error("HomeController-error:doctorQuestionService.queryUnreadCount:"+e.getLocalizedMessage());
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

        Map<String,Object>  number = new HashMap<>();
        int unread = 0;
        int intervenNum = 0;
        int screenNum = 0;
        int followNum = 0;
        try {
            unread = doctorQuestionService.queryUnreadCount(uid);// 杜宽心的接口
            intervenNum = doctorIntervenService.countHasInterventionByDoctorId(uid);//异常干预
            screenNum = screeningService.getRemindCount(uid);
            followNum = followRemindService.getRemindCount(uid);
        }catch (Exception e){
            log.error("HomeController-error:doctorQuestionService.queryUnreadCount:"+e.getLocalizedMessage());
        }
        number.put("signed_num",getNumStr(1088));//我的签约-签约人数 Todo
        number.put("group_num",getNumStr(22));//我的分组-分组数 Todo
        number.put("screened_num",getNumStr(screenNum));//筛查提醒-已筛查数
        number.put("intervened_num",getNumStr(intervenNum));//异常干预-已干预人数
        number.put("follow_num",getNumStr(followNum));//随访提醒-已随访人数
        number.put("ask_answered",getNumStr(45));//问诊回答—已回答数 Todo
        number.put("ask_unread",getNumStr(unread));//问诊回答-未读数

        body.setData(number);

        return body;
    }

    public String getNumStr(int number){
        if(number<=999){
            return String.valueOf(number);
        }else{
            return "999+";
        }
    }

}

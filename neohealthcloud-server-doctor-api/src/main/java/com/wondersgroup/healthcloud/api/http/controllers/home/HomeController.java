package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.question.DoctorQuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}

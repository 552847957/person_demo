package com.wondersgroup.healthcloud.api.http.controllers.identify;

import com.wondersgroup.healthcloud.api.http.dto.identify.HealthQuestionAPIEnity;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.identify.PhysicalIdentifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuchunliu on 2016/8/16.
 */
@RestController
@RequestMapping(value = "/api/physicalIdentify")
public class PhysicalIdentifyController {
    @Autowired
    private PhysicalIdentifyService physicalIdentifyService;


    @PostMapping
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<List<HealthQuestionAPIEnity>> doPhysiqueIdentify(
            @RequestBody String request) {

        JsonResponseEntity<List<HealthQuestionAPIEnity>> response = new JsonResponseEntity<List<HealthQuestionAPIEnity>>();

        JsonKeyReader reader = new JsonKeyReader(request);
        String registerid =  reader.readString("registerid", true);
        String content = reader.readString("content", false);

        List<HealthQuestionAPIEnity> list = new ArrayList<HealthQuestionAPIEnity>();
        String info = physicalIdentifyService.physiqueIdentify(registerid ,content);
        if(null != info && !"".equals(info)){
            String[] arr = info.split(",");
            for (String physique : arr) {
                list.add(new HealthQuestionAPIEnity(physique));
            }
        }
        response.setData(list);
        return response;
    }

    @GetMapping(value = "/list")
    @VersionRange
    public JsonListResponseEntity getPhysiqueIdentify(
            @RequestParam String registerid,@RequestParam String flag) {

        JsonListResponseEntity response = new JsonListResponseEntity();

        return response;
    }

    @GetMapping("/info")
    @VersionRange
    public JsonResponseEntity<List<HealthQuestionAPIEnity>> getPhysiqueIdentifyInfo(
            @RequestParam String id) {

        JsonResponseEntity<List<HealthQuestionAPIEnity>> response = new JsonResponseEntity<List<HealthQuestionAPIEnity>>();


        return response;
    }
}

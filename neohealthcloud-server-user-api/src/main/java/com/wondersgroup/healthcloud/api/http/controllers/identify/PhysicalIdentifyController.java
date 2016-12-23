package com.wondersgroup.healthcloud.api.http.controllers.identify;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.identify.HealthQuestionAPIEnity;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.identify.HealthQuestion;
import com.wondersgroup.healthcloud.jpa.repository.identify.HealthQuestionRepository;
import com.wondersgroup.healthcloud.services.identify.PhysicalIdentifyService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/8/16.
 */
@RestController
@RequestMapping(value = "/api/physicalIdentify")
public class PhysicalIdentifyController {
    @Autowired
    private PhysicalIdentifyService physicalIdentifyService;

    @Autowired
    private HealthQuestionRepository healthQuestionRepo;

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
            @RequestParam(required = true) String registerid,
            @RequestParam(defaultValue = "1") Integer flag) {
        int pageSize = 10;
        Pageable pageable = new PageRequest(flag-1,pageSize, Sort.Direction.DESC,"testtime");
        List<HealthQuestion> list = healthQuestionRepo.findQuestionList(registerid,pageable);
        Boolean hasMore = false;
        if(list.size() == pageSize && healthQuestionRepo.getTotalQuestion(registerid) > pageSize * flag){
            hasMore = true;
            flag++;
        }

        List<Map> result = Lists.newArrayList();
        for(HealthQuestion question : list){
            ImmutableMap map = null;
            if(DateTime.now().toString("yyyy-MM-dd").equals(new DateTime(question.getTesttime()).toString("yyyy-MM-dd"))){
                map = ImmutableMap.of("id",question.getId(),"time","今天",
                        "type",StringUtils.isEmpty(question.getResult())?"":question.getResult().split(",")[0]);
            }else{
                map = ImmutableMap.of("id",question.getId(),"time",new DateTime(question.getTesttime()).toString("yyyy-MM-dd"),
                        "type",StringUtils.isEmpty(question.getResult())?"":question.getResult().split(",")[0]);
            }
            result.add(map);
        }

        JsonListResponseEntity response = new JsonListResponseEntity();
        response.setContent(result,hasMore,null,flag.toString());
        return response;
    }

    /**
     * 查看详情
     * @return
     */
    @GetMapping("/detail")
    @VersionRange
    public JsonResponseEntity<List<HealthQuestionAPIEnity>> detail(
            @RequestParam String id) {

        JsonResponseEntity<List<HealthQuestionAPIEnity>> response = new JsonResponseEntity<List<HealthQuestionAPIEnity>>();
        HealthQuestion question = healthQuestionRepo.findOne(id);
        if(null == question){
            return null;
        }
        List<HealthQuestionAPIEnity> list = new ArrayList<HealthQuestionAPIEnity>();

        if(!StringUtils.isEmpty(question.getResult())){
            for (String physique : question.getResult().split(",")) {
                list.add(new HealthQuestionAPIEnity(physique));
            }
        }
        response.setData(list);
        return response;
    }


    /**
     * 查看详情
     * @return
     */
    @GetMapping("/recent")
    @VersionRange
    public JsonResponseEntity<List<HealthQuestionAPIEnity>> recent(
            @RequestParam String registerid) {

        JsonResponseEntity<List<HealthQuestionAPIEnity>> response = new JsonResponseEntity<List<HealthQuestionAPIEnity>>();
        HealthQuestion healthQuestion = physicalIdentifyService.getRecentPhysicalIdentify(registerid);
        List<HealthQuestionAPIEnity> list = new ArrayList<HealthQuestionAPIEnity>();
        if(null != healthQuestion && !StringUtils.isEmpty(healthQuestion.getResult())){
            for (String physique : healthQuestion.getResult().split(",")) {
                list.add(new HealthQuestionAPIEnity(physique));
            }
        }
        response.setData(list);
        return response;
    }
}

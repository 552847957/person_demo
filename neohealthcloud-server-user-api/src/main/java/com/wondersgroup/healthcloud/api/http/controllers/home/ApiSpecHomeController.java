package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicListDto;
import com.wondersgroup.healthcloud.services.home.HomeService;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.CenterAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.SideAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.FamilyHealthDTO;
import com.wondersgroup.healthcloud.services.home.dto.functionIcons.FunctionIconsDTO;
import com.wondersgroup.healthcloud.services.home.dto.modulePortal.ModulePortalDTO;
import com.wondersgroup.healthcloud.services.home.dto.specialService.SpecialServiceDTO;
import com.wondersgroup.healthcloud.services.home.impl.TopicManageServiceImpl;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xianglinhai on 2016/12/14.
 */
@RestController
@RequestMapping("/api/spec")
public class ApiSpecHomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private TopicManageServiceImpl topicManageService;

    @Value("${api.measure.url}")
    private String API_MEASURE_URL ;

    @Value("${api.userhealth.record.url}")
    private String API_USERHEALTH_RECORD_URL;


    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity index(@RequestHeader(value = "main-area", required = true) String mainArea,
                                                @RequestHeader(value = "spec-area", required = false) String specArea,
                                                @RequestHeader(value = "app-version", required = true) String version,
                                                @AccessToken(required = false, guestEnabled = true) Session session) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();

        //主要功能区
        List<FunctionIconsDTO> functionIcons = homeService.findFunctionIconsDTO(session,version,mainArea,specArea);
        data.put("functionIcons",functionIcons);

        //特色服务
        List<SpecialServiceDTO>  specialService = homeService.findSpecialServiceDTO(session,version,mainArea,specArea);
        data.put("specialService",specialService);

        //中央区广告
        List<CenterAdDTO> advertisements = homeService.findCenterAdDTO(mainArea);
        data.put("advertisements",advertisements);

         //侧边浮层广告
        SideAdDTO sideAd = homeService.findSideAdDTO(mainArea);
        data.put("sideAd",sideAd);

        //家庭健康栏目
        FamilyHealthDTO familyHealth = homeService.findfamilyHealth("8a81c1fb555cab530155e7ef379e00a1",API_MEASURE_URL,API_USERHEALTH_RECORD_URL);//用户注册id
        data.put("familyHealth",familyHealth);

        //热门话题
        List<TopicListDto> hotTopic = topicManageService.getHotTopicList("8a81c1fb555cab530155e7ef379e00a1",mainArea);
        data.put("hotTopic",hotTopic);

        //云头条
        CloudTopLineDTO cloudTopLine = homeService.findCloudTopLine();
        data.put("cloudTopLine",cloudTopLine);

        //慢病模块
        List<ModulePortalDTO> modulePortal = homeService.findModulePortal();
        data.put("modulePortal",modulePortal);

        result.setCode(0);
        result.setData(data);
        return result;
    }


}

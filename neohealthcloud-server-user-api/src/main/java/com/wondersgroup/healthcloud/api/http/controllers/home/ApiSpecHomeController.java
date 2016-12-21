package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicListDto;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.home.HomeService;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.CenterAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.SideAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.FamilyHealthDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.FamilyMemberDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.UserHealthDTO;
import com.wondersgroup.healthcloud.services.home.dto.functionIcons.FunctionIconsDTO;
import com.wondersgroup.healthcloud.services.home.dto.modulePortal.ModulePortalDTO;
import com.wondersgroup.healthcloud.services.home.dto.specialService.SpecialServiceDTO;
import com.wondersgroup.healthcloud.services.home.impl.TopicManageServiceImpl;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xianglinhai on 2016/12/14.
 */
@RestController
@RequestMapping("/api/spec")
public class ApiSpecHomeController {
    private static final Logger logger = LoggerFactory.getLogger(ApiSpecHomeController.class);
    @Autowired
    private HomeService homeService;

    @Autowired
    private TopicManageServiceImpl topicManageService;

    @Autowired
    RegisterInfoRepository registerInfoRepo;

    @Autowired
    private AppConfigService appConfigService;


    @Value("${internal.api.service.measure.url}")
    private  String API_MEASURE_URL;

    @Value("${internal.api.service.healthrecord.url}")
    private  String API_USERHEALTH_RECORD_URL;

    @Value("${api.vaccine.url}")
    private  String API_VACCINE_URL;



    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity index(@RequestHeader(value = "main-area", required = true) String mainArea,
                                    @RequestHeader(value = "spec-area", required = false) String specArea,
                                    @RequestHeader(value = "app-version", required = true) String version,
                                    @RequestParam(value = "uid", required = false) String uid,
                                    @AccessToken(required = false, guestEnabled = true) Session session) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();

        RegisterInfo registerInfo = null;
        if (StringUtils.isNotBlank(uid)) {
            registerInfo = registerInfoRepo.findOne(uid);
        }


        //主要功能区
        List<FunctionIconsDTO> functionIcons = null;
        try {
            functionIcons = homeService.findFunctionIconsDTO(session, version, mainArea, specArea);
        } catch (Exception e) {
            logger.error(" msg " + e.getMessage());
        }

        functionIcons = CollectionUtils.isEmpty(functionIcons) ? new ArrayList<FunctionIconsDTO>(0) : functionIcons;
        data.put("functionIcons", functionIcons);

        //特色服务
        List<SpecialServiceDTO> specialService = null;
        try {
            specialService = homeService.findSpecialServiceDTO(session, version, mainArea, specArea);
        } catch (Exception e) {
            logger.error(" msg " + e.getMessage());
        }
        specialService = CollectionUtils.isEmpty(specialService) ? new ArrayList<SpecialServiceDTO>(0) : specialService;
        data.put("specialService", specialService);

        //中央区广告
        List<CenterAdDTO> advertisements = null;
        try {
            advertisements = homeService.findCenterAdDTO(mainArea);
        } catch (Exception e) {
            logger.error(" msg " + e.getMessage());
        }
        advertisements = CollectionUtils.isEmpty(advertisements) ? new ArrayList<CenterAdDTO>(0) : advertisements;
        data.put("advertisements", advertisements);

        //侧边浮层广告
        SideAdDTO sideAd = null;
        try {
            sideAd = homeService.findSideAdDTO(mainArea, null);
        } catch (Exception e) {
            logger.error(" msg " + e.getMessage());
        }
        sideAd = sideAd == null ? new SideAdDTO() : sideAd;
        data.put("sideAd", sideAd);


        FamilyHealthDTO familyHealth = null;

        if (null != registerInfo) {
            Map<String, Object> urlMap = new HashMap<String, Object>();
            urlMap.put("apiMeasureUrl", API_MEASURE_URL);
            urlMap.put("apiUserhealthRecordUrl", API_USERHEALTH_RECORD_URL);
            urlMap.put("apiVaccineUrl", API_VACCINE_URL);

            try {
                familyHealth = homeService.findfamilyHealth(registerInfo, urlMap);
            } catch (Exception e) {
                logger.error(" msg " + e.getMessage());
            }
        }

        if (null == familyHealth) {
            familyHealth = new FamilyHealthDTO(new UserHealthDTO(), new FamilyMemberDTO());
        }

        //家庭健康栏目
        data.put("familyHealth", familyHealth);

        List<TopicListDto> hotTopic = null;
        if (null != registerInfo) {
            try {
                hotTopic = topicManageService.getHotTopicList(registerInfo.getRegisterid(), mainArea);
            } catch (Exception e) {
                logger.error(" msg " + e.getMessage());
            }
        }

        hotTopic = CollectionUtils.isEmpty(hotTopic) ? new ArrayList<TopicListDto>(0) : hotTopic.size() > 5 ? hotTopic.subList(0,5):hotTopic;
        //热门话题
        data.put("hotTopic", hotTopic);

        //云头条
        CloudTopLineDTO cloudTopLine = null;
        try {
            cloudTopLine = homeService.findCloudTopLine();
        } catch (Exception e) {
            logger.error(" msg " + e.getMessage());
        }

        cloudTopLine = cloudTopLine == null ? new CloudTopLineDTO() : cloudTopLine;
        data.put("cloudTopLine", cloudTopLine);

        //慢病模块
        List<ModulePortalDTO> modulePortal = null;
        try {
            modulePortal = homeService.findModulePortal();
        } catch (Exception e) {
            logger.error(" msg " + e.getMessage());
        }
        modulePortal = CollectionUtils.isEmpty(modulePortal) ? new ArrayList<ModulePortalDTO>(0) : modulePortal;
        data.put("modulePortal", modulePortal);

        result.setCode(0);
        result.setData(data);
        result.setMsg("获取数据成功");
        return result;
    }

    @RequestMapping(value = "/telephoneAd", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity telephoneAd(@RequestHeader(value = "main-area", required = true) String mainArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        JsonNode jsonNode = null;

        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.floatTelephone");
        if (appConfig != null) {
            try {
                String telephoneAd = appConfig.getData();
                ObjectMapper om = new ObjectMapper();
                 jsonNode = om.readTree(telephoneAd);
            } catch (Exception ex) {
                logger.error("telephoneAd " + ex.getMessage());
            }
        }

        if (null != jsonNode) {
            result.setCode(0);
            result.setData(jsonNode);
            result.setMsg("获取数据成功");
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关数据！");
        }

        return result;
    }
}
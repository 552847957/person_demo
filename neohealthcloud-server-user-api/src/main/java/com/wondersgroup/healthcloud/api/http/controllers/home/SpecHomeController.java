package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.faq.FaqDTO;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.article.dto.NewsArticleListAPIEntity;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.faq.FaqService;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.imagetext.dto.BasicImageTextDTO;
import com.wondersgroup.healthcloud.services.imagetext.dto.ImageTextPositionDTO;
import com.wondersgroup.healthcloud.services.notice.NoticeService;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.security.ServiceUrlPlaceholderResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/30.
 */
@RestController
@RequestMapping("/api/spec/home")
public class SpecHomeController {
    private static final Logger log = Logger.getLogger(HomeController.class);

    @Autowired
    private ImageTextService imageTextService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private ManageNewsArticleService manageNewsArticleService;

    @Autowired
    private FaqService faqService;

    @Autowired
    RegisterInfoRepository registerInfoRepo;

    private static final String requestStationNearby = "%s/api/exam/station/nearby?";
    private static final String requestStationDetail = "%s/api/exam/station/detail?id=%s";

    @Value("${internal.api.service.measure.url}")
    private String host;

    private RestTemplate template = new RestTemplate();

    @Autowired
    private ServiceUrlPlaceholderResolver serviceUrlPlaceholderResolver;

    @RequestMapping(value = "/bannerFunctionAds", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity bannerFunctionAds(@RequestHeader(value = "main-area", required = true) String mainArea,
                                                @RequestHeader(value = "spec-area", required = false) String specArea,
                                                @RequestHeader(value = "app-version", required = true) String version,
                                                @AccessToken(required = false, guestEnabled = true) Session session) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();

        // 首页Banner
        ImageText imgTextA = new ImageText();
        imgTextA.setAdcode(ImageTextEnum.HOME_BANNER.getType());
        List<ImageText> imageTextsA = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgTextA);
        if (imageTextsA != null && imageTextsA.size() > 0) {
            List banners = new ArrayList();
            int flag = 5;
            if (imageTextsA.size() < flag) {
                flag = imageTextsA.size();
            }
            for (int i = 0; i < flag; i++) {
                BasicImageTextDTO bit = new BasicImageTextDTO(imageTextsA.get(i));
                banners.add(bit);
            }
            data.put("banners", banners);
        } else {// 当指定区域未获取到banner时，从主区域获取
            imageTextsA = imageTextService.findImageTextByAdcodeForApp(mainArea, null, imgTextA);
            if (imageTextsA != null && imageTextsA.size() > 0) {
                List banners = new ArrayList();
                for (ImageText imageText : imageTextsA) {
                    BasicImageTextDTO bit = new BasicImageTextDTO(imageText);
                    banners.add(bit);
                }
                data.put("banners", banners);
            }
        }

        // 首页功能栏
        List<ImageText> imageTextsB = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_HOME_FUNCTION.getType(), version);
        if (imageTextsB != null && imageTextsB.size() > 0) {
            List functionIcons = new ArrayList();
            Map map = null;
            for (ImageText imageText : imageTextsB) {
                map = new HashMap();
                map.put("imgUrl", imageText.getImgUrl());
                map.put("hoplink", serviceUrlPlaceholderResolver.parseUrl(imageText.getHoplink(), session));
                map.put("mainTitle", imageText.getMainTitle());
                map.put("subTitle", imageText.getSubTitle());
                functionIcons.add(map);
            }
            data.put("functionIcons", functionIcons);
        }

        // 首页浮动广告及在线客服
        ImageText imgTextD = new ImageText();
        imgTextD.setAdcode(ImageTextEnum.HOME_FLOAT_AD.getType());
        List<ImageText> imageTextsD = imageTextService.findImageTextByAdcodeForApp(mainArea, null, imgTextD);
        if (imageTextsD != null && imageTextsD.size() > 0) {
            data.put("sideAd", new ImageTextPositionDTO(imageTextsD.get(0)));
        }

        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.floatTelephone");
        if (appConfig != null) {
            try {
                String telephoneAd = appConfig.getData();
                ObjectMapper om = new ObjectMapper();
                JsonNode jsonNode = om.readTree(telephoneAd);
                data.put("telephoneAd", jsonNode);
            } catch (Exception ex) {

            }
        }

        if (data.size() > 0) {
            result.setData(data);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @RequestMapping(value = "/appTips", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity appTips(@RequestHeader(value = "main-area", required = true) String mainArea,
                                      @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        Notice notice = noticeService.findNoticeByAreaForApp(mainArea, specArea);
        if (notice != null) {
            Map data = new HashMap();
            data.put("content", notice.getContent());
            data.put("hoplink", notice.getHoplink());
            result.setData(data);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置信息");
        }

        return result;
    }

    @RequestMapping(value = "/newsAndQuestions", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity newsAndQuestions(@RequestHeader(value = "main-area", required = true) String mainArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();
        try {
            List<NewsArticleListAPIEntity> newsArticleList = manageNewsArticleService.findArticleForFirst(mainArea, 0, 10);
            if (newsArticleList != null && newsArticleList.size() > 0) {
                data.put("news", newsArticleList);
            }
        } catch (Exception ex) {
            log.error("SpecHomeController.newsAndQuestions Error --> manageNewsArticleService.findArticleForFirst -->" + ex.getLocalizedMessage());
        }

        try {
            List<Faq> faqList = faqService.findHomeFaqList();
            if (faqList != null) {
                List<FaqDTO> questions = Lists.newArrayList();
                for (Faq faq : faqList) {
                    FaqDTO faqDTO = new FaqDTO(faq);
                    //查询回答数
                    int commentCount = faqService.countCommentByQid(faq.getQId());
                    faqDTO.setCommentCount(commentCount);
                    faqDTO.setAskTime(DateFormatter.questionListDateFormat(faq.getAskDate()));
                    questions.add(faqDTO);
                }
                data.put("questions", questions);
            }
        } catch (Exception ex) {
            log.error("SpecHomeController.newsAndQuestions Error --> faqService.findHomeFaqList -->" + ex.getLocalizedMessage());
        }

        result.setData(data);
        return result;
    }

    @RequestMapping(value = "/specSerMeasuringPoint", method = RequestMethod.GET)
    @WithoutToken
    @VersionRange
    public JsonResponseEntity specSerMeasuringPoint(@AccessToken(required = false, guestEnabled = true) Session session,
                                                    @RequestHeader(name = "main-area", required = true) String mainArea,
                                                    @RequestHeader(name = "spec-area", required = false) String specArea,
                                                    @RequestHeader(name = "app-version", required = true) String appVersion,
                                                    @RequestParam String areaCode,
                                                    Double longitude, Double latitude, Boolean need, Integer flag) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map<String, Object> data = new HashMap<>();
        List<ImageText> imageTextsB = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_HOME_SPECIAL_SERVICE.getType(), appVersion);
        if (imageTextsB != null && imageTextsB.size() > 0) {
            List specialServices = new ArrayList();
            Map map = null;

            String idCard = null;
            int loginOrRealName = 0;// 0:需登录,1:需实名制,2:正常
            if (session != null && StringUtils.isNotEmpty(session.getUserId())) {
                String userId = session.getUserId();
                RegisterInfo registerInfo = registerInfoRepo.findOne(userId);
                if (registerInfo != null) {
                    // 未实名认证
                    if (!"1".equals(registerInfo.getIdentifytype()) && !"2".equals(registerInfo.getIdentifytype())) {
                        loginOrRealName = 1;
                    } else {
                        loginOrRealName = 2;
                        idCard = registerInfo.getPersoncard();
                    }
                }
            }
            for (ImageText imageText : imageTextsB) {
                map = new HashMap();
                map.put("imgUrl", imageText.getImgUrl());
                if (imageText.getHoplink() != null && imageText.getHoplink().contains("{sfzh}")) {// 需获取身份证
                    if (loginOrRealName == 2) {
                        map.put("loginOrRealName", 2);
                        map.put("hoplink", imageText.getHoplink().replace("{sfzh}", idCard));
                    } else {
                        map.put("loginOrRealName", loginOrRealName);
                        map.put("hoplink", imageText.getHoplink().replace("{sfzh}", ""));
                    }
                } else {// 不需要身份证信息
                    map.put("loginOrRealName", 2);
                    map.put("hoplink", imageText.getHoplink());
                }

                map.put("mainTitle", imageText.getMainTitle());
                map.put("subTitle", imageText.getSubTitle());
                specialServices.add(map);
            }
            data.put("specialService", specialServices);
        }

        //String url = String.format(requestStationNearby, host) + "areaCode=" + areaCode;
        String url = String.format(requestStationNearby, host) + "areaCode=310100";
        if (null != longitude && null != latitude) {
            url += "&longitude=" + longitude +
                    "&latitude=" + latitude;
        }
        if (need != null) {
            url += "&need=" + need;
        }
        if (flag != null) {
            url += "&flag=" + flag;
        }

        try {
            ResponseEntity<Map> response = template.getForEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    JsonResponseEntity jsonResponseEntity = formatResponse(response.getBody());
                    ObjectMapper objectMapper = new ObjectMapper();
                    String rtnStr = objectMapper.writeValueAsString(jsonResponseEntity);
                    JsonNode rtnJson = objectMapper.readTree(rtnStr);
                    JsonNode measuringPoint = rtnJson.get("data").get("content").get(0);
                    if (measuringPoint != null) {
                        ((ObjectNode) measuringPoint).put("description", getRandomDesc());
                        data.put("measuringPoint", measuringPoint);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("SpecHomeController error --> " + ex.getLocalizedMessage());
        }
        result.setData(data);
        return result;
    }

    private JsonResponseEntity formatResponse(Map responseBody) {
        if (0 != (int) responseBody.get("code")) {
            return new JsonResponseEntity(500, "信息获取失败");
        }
        JsonResponseEntity<Object> result = new JsonResponseEntity<>(0, null);
        result.setData(responseBody.get("data"));
        return result;
    }

    private String getRandomDesc() {
        String[] subTitles = {"健康指标免费测，健康管理随时做", "家门口的健康指标免费测量中心", "在这里，健康设备免费测"};
        return subTitles[(int) (Math.random() * 10) % subTitles.length];
    }
}

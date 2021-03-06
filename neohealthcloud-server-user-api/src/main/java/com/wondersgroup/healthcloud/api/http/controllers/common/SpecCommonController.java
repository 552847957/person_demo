package com.wondersgroup.healthcloud.api.http.controllers.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.utils.CommonUtils;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.common.utils.UploaderUtil;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.enums.TabServiceTypeEnum;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.home.HomeService;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeTabServiceDTO;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.imagetext.dto.LoadingImageDTO;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/30.
 * 上海健康云全局接口
 */
@RestController
@RequestMapping("/api/spec/common")
public class SpecCommonController {
    private static final Logger log = Logger.getLogger(CommonController.class);

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private ImageTextService imageTextService;

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;

    @Autowired
    private HomeService homeService;

    @GetMapping(value = "/appConfig")
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Map<String, Object>> appConfig(@RequestHeader(value = "platform", required = true) String platform,
                                                             @RequestHeader(name = "main-area", required = true) String mainArea,
                                                             @RequestHeader(name = "spec-area", required = false) String specArea,
                                                             @RequestHeader(value = "app-version", required = false) String appVersion) {
        JsonResponseEntity<Map<String, Object>> result = new JsonResponseEntity<>();
        Map<String, Object> data = new HashMap<>();
        // 全局接口不需指定区级区域ID
        data.put("common", getAppConfig(mainArea, null, platform));

        // 更新接口无需校验 specArea
        AppConfig acUpdate = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.appUpdate");
        if ("0".equals(platform)) {// iOS 升级配置
            acUpdate = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.appUpdate.ios");
        }
        if (acUpdate != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode content = objectMapper.readTree(acUpdate.getData());
                String lastVersion = content.get("lastVersion") == null ? "" : content.get("lastVersion").asText();
                Boolean hasUpdate = CommonUtils.compareVersion(appVersion, lastVersion);
                if (hasUpdate) {
                    Boolean forceUpdate = false;
                    String forceUpdateVersion = content.get("enforceUpdate") == null ? "" : content.get("enforceUpdate").asText();
                    if (!com.qiniu.util.StringUtils.isNullOrEmpty(forceUpdateVersion) && forceUpdateVersion.split(",").length == 2) {
                        forceUpdate = CommonUtils.compareVersion(forceUpdateVersion.split(",")[0], appVersion) && CommonUtils.compareVersion(appVersion, forceUpdateVersion.split(",")[1]);
                    }
                    String updateMsg = content.get("updateMsg") == null ? "" : content.get("updateMsg").asText();
                    String downloadUrl = content.get("downloadUrl") == null ? "" : content.get("downloadUrl").asText();
                    String iosDownloadUrl = content.get("iosDownloadUrl") == null ? "" : content.get("iosDownloadUrl").asText();

                    Map appUpdate = new HashMap();
                    appUpdate.put("hasUpdate", hasUpdate);
                    appUpdate.put("forceUpdate", forceUpdate);
                    appUpdate.put("lastVersion", lastVersion);
                    appUpdate.put("updateMsg", updateMsg);
                    if ("0".equals(platform)) {// iOS
                        appUpdate.put("iosUrl", iosDownloadUrl);
                    } else {
                        appUpdate.put("androidUrl", downloadUrl);
                    }
                    data.put("appUpdate", appUpdate);
                }
            } catch (Exception ex) {
                log.error("CommonController.appConfig Error -->" + ex.getLocalizedMessage());
            }
        }

        AppConfig registrationConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.registration");
        if(registrationConfig != null){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode content = objectMapper.readTree(registrationConfig.getData());
                String registrationNotice = content.get("registrationRule") == null ? "" : content.get("registrationRule").asText();
                String addContactDesc = content.get("addContactDesc") == null ? "" : content.get("addContactDesc").asText();
                String registrationTel = content.get("registrationTel") == null ? "" : content.get("registrationTel").asText();
                String registrationTelDesc = content.get("registrationTelDesc") == null ? "" : content.get("registrationTelDesc").asText();
                Map registration = new HashMap();
                registration.put("registration_notice", registrationNotice);
                registration.put("add_contact_desc", addContactDesc);
                registration.put("registration_tel", registrationTel);
                registration.put("registration_tel_desc", registrationTelDesc);
                data.put("registration", registration);

            }catch (Exception ex){
                log.error("CommonController.appConfig Error -->" + ex.getLocalizedMessage());
            }

        }

        ImageText imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.LOADING_IMAGE.getType());
        List<ImageText> imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, null, imgText);
        if (imageTexts != null && imageTexts.size() > 0) {
            ImageText imageText = imageTexts.get(0);
            LoadingImageDTO loadingImageDTO = new LoadingImageDTO(imageText);
            data.put("ads", loadingImageDTO);
        }

        // APP分享
        AppConfig appShare = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.shareUrl");
        try {
            if (appShare != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode content = objectMapper.readTree(appShare.getData());
                data.put("share", content);
            }
        } catch (IOException e) {
            log.info("AppConfig --> " + e.getLocalizedMessage());
        }

        result.setData(data);
        return result;
    }

    @RequestMapping(value = "/getQiniuToken", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Map<String, Object>> qiniuConfig() {
        JsonResponseEntity<Map<String, Object>> response = new JsonResponseEntity<Map<String, Object>>();
        Map<String, Object> map = Maps.newHashMap();
        map.put("token", UploaderUtil.getUpToken());
        map.put("expires", UploaderUtil.expires);
        map.put("domain", UploaderUtil.domain);
        response.setData(map);
        return response;
    }

    @RequestMapping(value = "/appNavigationBar", method = RequestMethod.GET)
    @VersionRange(to = "4.3")
    @WithoutToken
    public JsonResponseEntity getNavigationBar(@RequestHeader(value = "main-area", required = true) String mainArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        ImageText imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.NAVIGATION_BAR.getType());
        List<ImageText> imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, null, imgText);
        if (imageTexts != null && imageTexts.size() > 0) {
            List<String> navigationBars = new ArrayList<>();
            for (ImageText imageText : imageTexts) {
                navigationBars.add(imageText.getImgUrl());
            }
            result.setData(navigationBars);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置信息！");
        }
        return result;
    }

    @RequestMapping(value = "/appNavigationBar", method = RequestMethod.GET)
    @WithoutToken
    @VersionRange(from = "4.3")
    public JsonResponseEntity getNavigationBarForVersion(@RequestHeader(value = "main-area", required = true) String mainArea,
                                                         @RequestHeader(value = "app-version", required = true) String version) {
        JsonResponseEntity result = new JsonResponseEntity();
            Map paramMap = new HashMap();
            paramMap.put("version",version);
            List<HomeTabServiceDTO> list = homeService.findHomeTableService(paramMap);

        List<HomeTabServiceDTO> highlight = new ArrayList<HomeTabServiceDTO>(); //高亮
        List<HomeTabServiceDTO> noHighlight = new ArrayList<HomeTabServiceDTO>(); //非高亮
        List<HomeTabServiceDTO> backGround = new ArrayList<HomeTabServiceDTO>();//背景

        if(CollectionUtils.isEmpty(list)){
            result.setCode(1000);
            result.setMsg("未查询到相关配置信息！");
        }
        for (HomeTabServiceDTO dto : list) {
            if(TabServiceTypeEnum.HIGHTLIGHT.getType().equals(dto.getTabType())){
                highlight.add(new HomeTabServiceDTO(dto.getImgUrl()));
            }
            if(TabServiceTypeEnum.NO_HIGHTLIGHT.getType().equals(dto.getTabType())){
                noHighlight.add(new HomeTabServiceDTO(dto.getImgUrl()));
            }
            if(TabServiceTypeEnum.BACKGROUND.getType().equals(dto.getTabType())){
                backGround.add(new HomeTabServiceDTO(dto.getImgUrl()));
            }
        }
        return new JsonResponseEntity(0, "操作成功!", new TabData(highlight,noHighlight,backGround));
    }

    @RequestMapping(value = "/aboutApp", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity aboutApp(@RequestHeader(value = "main-area", required = true) String mainArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        try {
            AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.aboutApp");
            if (appConfig != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode aboutApp = objectMapper.readTree(appConfig.getData());
                result.setData(aboutApp);
            } else {
                result.setCode(1000);
                result.setMsg("未查询到相关配置信息！");
            }
        } catch (Exception ex) {
            log.error("CommonController.aboutApp Error -->" + ex.getLocalizedMessage());
            result.setCode(1000);
            result.setMsg("获取配置信息失败！");
        }
        return result;
    }

    private CommonDTO getAppConfig(String mainArea, String specArea, String platform) {
        CommonDTO common = new CommonDTO();
        common.setPublicKey(HttpWdUtils.publicKey);

        List<String> keyWords = new ArrayList<>();
        keyWords.add("app.common.consumer.hotline");//客服热线
        keyWords.add("app.common.help.center");// 帮助中心
        keyWords.add("app.common.userAgreement");// 用户协议
        keyWords.add("app.common.intellectualPropertyAgreement");// 知识产权协议
        keyWords.add("common.qr.code.url.ios");// ios邀请二维码
        keyWords.add("common.qr.code.url.android");// android邀请二维码
        keyWords.add("app.common.huidao.channel");// 汇道
        keyWords.add("app.common.huidao.appkey");// 汇道
        keyWords.add("app.common.huidao.sid");// 汇道
        keyWords.add("app.common.huidao.apiid");// 汇道
        keyWords.add("app.common.medicineCloudUrl");// 医药云
        keyWords.add("app.common.medicinePayUrl");// 医疗支出
        keyWords.add("app.common.recordUrl");// 市级健康档案
        keyWords.add("app.common.pointUrl");// 积分商城地址
        keyWords.add("app.common.isUmengEvent");// 友盟
        keyWords.add("app.common.isWdEvent2");// 内部统计
        keyWords.add("app.common.citizenUrl");// 市民云url
        keyWords.add("app.common.registerUrl");// 注册协议
        keyWords.add("app.common.wdTrinityKey");// 公司埋点key
        keyWords.add("app.common.wdTrinityIp");// 公司埋点ip
        keyWords.add("app.common.voiceTip");// 语音-提示
        keyWords.add("app.common.callCentUrl");// 在线客服链接
        keyWords.add("app.common.disclaimerUrl");// 健康档案说明文案
        keyWords.add("app.common.vrules");// 查看版规
        Map<String, String> cfgMap = appConfigService.findAppConfigByKeyWords(mainArea, specArea, keyWords);

        if (cfgMap != null) {
            common.setConsumerHotline(cfgMap.get("app.common.consumer.hotline"));
            if (StringUtils.isNotEmpty(cfgMap.get("app.common.help.center"))) {
                common.setHelpCenter(appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.help.center")));
            }
            if (StringUtils.isNotEmpty(cfgMap.get("app.common.userAgreement"))) {
                common.setUserAgreement(appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.userAgreement")));
            }
            if (StringUtils.isNotEmpty(cfgMap.get("app.common.intellectualPropertyAgreement"))) {
                common.setIpa(appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.intellectualPropertyAgreement")));
            }
            if (StringUtils.isNotEmpty(cfgMap.get("app.common.recordUrl"))) {
                common.setRecord_url(appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.recordUrl")));
            }
            if (StringUtils.isNotEmpty(cfgMap.get("app.common.vrules"))) {
                common.setVrules(appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.vrules")));
            }else {
                common.setVrules(appUrlH5Utils.buildBbsVrules());
            }
            if (platform.equalsIgnoreCase("0")) {
                common.setQrCode(cfgMap.get("common.qr.code.url.ios"));
            } else if (platform.equalsIgnoreCase("1")) {
                common.setQrCode(cfgMap.get("common.qr.code.url.android"));
            }
            // 汇道
            common.setHuiDaoChannelid(cfgMap.get("app.common.huidao.channel"));
            common.setHuiDaoAppkey(cfgMap.get("app.common.huidao.appkey"));
            common.setHuiDaoSid(cfgMap.get("app.common.huidao.sid"));
            common.setHuiDaoApiid(cfgMap.get("app.common.huidao.apiid"));

            common.setMedicineCloudUrl(cfgMap.get("app.common.medicineCloudUrl"));
            common.setMedicinePayUrl(cfgMap.get("app.common.medicinePayUrl"));
            common.setPointUrl(cfgMap.get("app.common.pointUrl"));
            common.setIsUmengEvent(cfgMap.get("app.common.isUmengEvent"));
            common.setIsWdEvent2(cfgMap.get("app.common.isWdEvent2"));
            common.setCitizenUrl(cfgMap.get("app.common.citizenUrl"));
            common.setRegisterUrl(cfgMap.get("app.common.registerUrl"));
            common.setWdTrinityKey(cfgMap.get("app.common.wdTrinityKey"));
            common.setWdTrinityIp(cfgMap.get("app.common.wdTrinityIp"));
            common.setVoiceTip(cfgMap.get("app.common.voiceTip"));
            common.setCallCentUrl(cfgMap.get("app.common.callCentUrl"));
            common.setDisclaimerUrl(cfgMap.get("app.common.disclaimerUrl"));
        }
        return common;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class CommonDTO {
        private String publicKey;// 公钥
        private String consumerHotline;// 客服热线
        private String helpCenter;// 帮助中心
        private String userAgreement;// 用户协议
        private String ipa;// 知识产权协议
        private String qrCode;// 分享二维码
        private String huiDaoChannelid;// 汇道
        private String huiDaoAppkey;// 汇道
        private String huiDaoSid;// 汇道
        private String huiDaoApiid;// 汇道
        private String medicineCloudUrl;// 医药云
        private String medicinePayUrl;// 医疗支出
        private String record_url;// 市级健康档案
        private String pointUrl;// 积分商城地址
        private String isUmengEvent;// 友盟
        private String isWdEvent2;// 内部统计
        private String citizenUrl;// 市民云url
        private String registerUrl;// 注册协议
        private String wdTrinityKey;// 公司埋点key
        private String wdTrinityIp;// 公司埋点ip
        private String voiceTip;// 语音-提示
        private String callCentUrl;// 在线客服是否显示
        private String disclaimerUrl;// 健康档案说明文案
        private String vrules;//圈子的版规
    }
}


@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class TabData{
    List<HomeTabServiceDTO> highlight = null; //高亮
    List<HomeTabServiceDTO> noHighlight = null; //非高亮
    List<HomeTabServiceDTO> backGround = null;//背景
    public TabData(){};
    public TabData(List<HomeTabServiceDTO> highlight,List<HomeTabServiceDTO> noHighlight,List<HomeTabServiceDTO> backGround ){
        this.highlight = highlight;
        this.noHighlight = noHighlight;
        this.backGround = backGround;

    };
}
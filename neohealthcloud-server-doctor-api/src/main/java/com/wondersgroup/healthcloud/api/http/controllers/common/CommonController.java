package com.wondersgroup.healthcloud.api.http.controllers.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.imagetext.dto.InterImageDTO;
import com.wondersgroup.healthcloud.services.imagetext.dto.LoadingImageDTO;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/12.
 */

@RestController
@RequestMapping("/api/common")
public class CommonController {
    private static final Logger log = Logger.getLogger(CommonController.class);
    
    @Autowired
    private AppConfigService appConfigService;
    
    @Autowired
    private ImageTextService imageTextService;

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;


    /**
     * APP获取启动数据
     */
    @RequestMapping(value = "/appConfig", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Map<String, Object>> appConfig(@RequestHeader(value = "platform", required = false) String platform,
                                                             @RequestHeader(value = "screen-width", required = false) String width,
                                                             @RequestHeader(value = "screen-height", required = false) String height,
                                                             @RequestHeader(value = "app-version", required = false) String appVersion,
                                                             @RequestHeader(value = "main-area", required = true) String mainArea,
                                                             @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity<Map<String, Object>> response = new JsonResponseEntity<>();
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> common = new HashMap<>();
        common.put("publicKey", HttpWdUtils.publicKey);
        
        List<String> keyWords = new ArrayList<>();
        keyWords.add("app.common.consumer.hotline");//客服热线
        keyWords.add("app.common.help.center");// 帮助中心
        keyWords.add("app.common.userAgreement");// 用户协议
        
        keyWords.add("app.common.versionDesc");// 关于我们中版本描述
        keyWords.add("app.common.versionDepartment");// 关于我们中部门描述
        
        keyWords.add("app.common.disclaimerUrl");//健康档案说明文案
        
        keyWords.add("app.common.recordUrl");

        keyWords.add("app.diabetesZone.open");//糖尿病专区开关
      
        keyWords.add("app.common.appUpdate");// APP更新
        Map<String, String> cfgMap = appConfigService.findAppConfigByKeyWords(mainArea, specArea, keyWords, "2");

        common.put("publicKey", HttpWdUtils.publicKey);
        if (cfgMap != null) {
            if (cfgMap.get("app.common.consumer.hotline") != null) {
                common.put("hotline", cfgMap.get("app.common.consumer.hotline"));
            }
            if (cfgMap.get("app.common.help.center") != null) {
                common.put("help_url", appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.help.center")));
            }
            if (cfgMap.get("app.common.userAgreement") != null) {
                common.put("register_url", appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.userAgreement")));
            }
            if(cfgMap.get("app.common.versionDesc") != null){
                common.put("version_desc", cfgMap.get("app.common.versionDesc"));
            }
            if(cfgMap.get("app.common.versionDepartment") != null){
                common.put("version_department", cfgMap.get("app.common.versionDepartment"));
            }
            if(cfgMap.get("app.common.disclaimerUrl") != null){
                common.put("disclaimerUrl", appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.disclaimerUrl")));
            }
            if(cfgMap.get("app.common.recordUrl") != null){
                common.put("record_url", appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.recordUrl")));
            }
            if(cfgMap.get("app.diabetesZone.open") != null){
                common.put("is_opne_diabetesZone", cfgMap.get("app.diabetesZone.open").equals("0") ? 0: 1);
            }else {
                common.put("is_opne_diabetesZone", 1);
            }
            common.put("diabetesZone_url", appUrlH5Utils.buildDiabetesZoneUrl());

            if (cfgMap.get("app.common.appUpdate") != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode content = objectMapper.readTree(cfgMap.get("app.common.appUpdate").toString());
                    String lastVersion = content.get("lastVersion") == null ? "" : content.get("lastVersion").asText();
                    Boolean hasUpdate = CommonUtils.compareVersion(appVersion, lastVersion);
                    if (hasUpdate) {
                        Boolean forceUpdate = false;
                        // 判断iOS是否强制升级
                        if("0".equals(platform)){
                            String iosForceUpdate = content.get("iosForceUpdate") == null ? "" : content.get("iosForceUpdate").asText();
                            if(StringUtils.isNotBlank(iosForceUpdate) && iosForceUpdate.equals("1")){
                                forceUpdate = true;
                            }
                        }else {
                            String forceUpdateVersion = content.get("enforceUpdate") == null ? "" : content.get("enforceUpdate").asText();
                            if (!com.qiniu.util.StringUtils.isNullOrEmpty(forceUpdateVersion) && forceUpdateVersion.split(",").length == 2) {
                                forceUpdate = CommonUtils.compareVersion(forceUpdateVersion.split(",")[0], appVersion) && CommonUtils.compareVersion(appVersion, forceUpdateVersion.split(",")[1]);
                            }
                        }
                        
                        String updateMsg = content.get("updateMsg") == null ? "" : content.get("updateMsg").asText();
                        String downloadUrl = content.get("downloadUrl") == null ? "" : content.get("downloadUrl").asText();
                        String iosDownloadUrl = content.get("iosDownloadUrl") == null ? "" : content.get("iosDownloadUrl").asText();

                        Map appUpdate = new HashMap();
                        appUpdate.put("hasUpdate", hasUpdate);
                        appUpdate.put("forceUpdate", forceUpdate);
                        appUpdate.put("lastVersion", lastVersion);
                        appUpdate.put("updateMsg", updateMsg);
                        appUpdate.put("androidUrl", downloadUrl);
                        appUpdate.put("iosUrl", iosDownloadUrl);
                        data.put("appUpdate", appUpdate);
                    }
                } catch (Exception ex) {
                    log.error("CommonController.appConfig Error -->" + ex.getLocalizedMessage());
                }
            }
        }

        //
        // 更新接口无需校验 specArea
        AppConfig acUpdate = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.appUpdate","2");
        if ("0".equals(platform)) {// iOS 升级配置
            acUpdate = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.appUpdate.ios","2");
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
        //
        
        ImageText imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.LOADING_IMAGE.getType());
        imgText.setSource("2");
        List<ImageText> imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgText);
        if (imageTexts != null && imageTexts.size() > 0) {
            ImageText imageText = imageTexts.get(0);
            LoadingImageDTO loadingImageDTO = new LoadingImageDTO(imageText);
            data.put("ads", loadingImageDTO);
        }
        
        List<ImageText> interImage = Lists.newArrayList();
        
        imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.AD_HOME.getType());
        imgText.setSource("2");
        
        imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgText);
        if(imageTexts != null){
            interImage.addAll(imageTexts);
        }
        
        
        imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.AD_CIRCLE.getType());
        imgText.setSource("2");
        imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgText);
        if(imageTexts != null){
            interImage.addAll(imageTexts);
        }
        
        imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.AD_DOCTOR_DETAIL.getType());
        imgText.setSource("2");
        imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgText);
        if(imageTexts != null){
            interImage.addAll(imageTexts);
        }
        
        imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.AD_QA_DETAIL.getType());
        imgText.setSource("2");
        imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgText);
        if(imageTexts != null){
            interImage.addAll(imageTexts);
        }
        
        List<InterImageDTO> interADs = Lists.newArrayList();
        if(interImage != null){
            for (ImageText imageText : interImage) {
                interADs.add(new InterImageDTO(imageText));
            }
        }

        common.put("interADs", interADs);
        data.put("common",common);
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
        response.setData(data);
        return response;

    }

    @RequestMapping(value = "/getQiniuToken", method = RequestMethod.GET)
    @WithoutToken
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> qiniuConfig() {
        JsonResponseEntity<Map<String, Object>> response = new JsonResponseEntity<Map<String, Object>>();
        Map<String, Object> map = Maps.newHashMap();
        map.put("token", UploaderUtil.getUpToken());
        map.put("expires", UploaderUtil.expires);
        map.put("domain", UploaderUtil.domain);
        response.setData(map);
        return response;
    }



}

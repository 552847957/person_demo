package com.wondersgroup.healthcloud.api.http.controllers.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
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
import com.wondersgroup.healthcloud.services.imagetext.dto.LoadingImageDTO;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

        List<String> keyWords = new ArrayList<>();
        keyWords.add("app.common.consumer.hotline");//客服热线
        keyWords.add("app.common.help.center");// 帮助中心
        keyWords.add("app.common.userAgreement");// 用户协议
        keyWords.add("app.common.intellectualPropertyAgreement");// 知识产权协议

        keyWords.add("app.common.appUpdate");// APP更新
        Map<String, String> cfgMap = appConfigService.findAppConfigByKeyWords(mainArea, specArea, keyWords);

        Map<String, Object> common = new HashMap<>();
        common.put("publicKey", HttpWdUtils.publicKey);
        if (cfgMap != null) {
            if (cfgMap.get("app.common.consumer.hotline") != null) {
                common.put("consumerHotline", cfgMap.get("app.common.consumer.hotline"));
            }
            if (cfgMap.get("app.common.help.center") != null) {
                common.put("helpCenter", appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.help.center")));
            }
            if (cfgMap.get("app.common.userAgreement") != null) {
                common.put("userAgreement", appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.userAgreement")));
            }
            if (cfgMap.get("app.common.intellectualPropertyAgreement") != null) {
                common.put("ipa", appUrlH5Utils.buildBasicUrl(cfgMap.get("app.common.intellectualPropertyAgreement")));
            }
            data.put("common", common);

            if (cfgMap.get("app.common.appUpdate") != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode content = objectMapper.readTree(cfgMap.get("app.common.appUpdate").toString());
                    String lastVersion = content.get("lastVersion") == null ? "" : content.get("lastVersion").asText();
                    Boolean hasUpdate = compareVersion(appVersion, lastVersion);
                    if (hasUpdate) {
                        Boolean forceUpdate = false;
                        String forceUpdateVersion = content.get("enforceUpdate") == null ? "" : content.get("enforceUpdate").asText();
                        if (!com.qiniu.util.StringUtils.isNullOrEmpty(forceUpdateVersion) && forceUpdateVersion.split(",").length == 2) {
                            forceUpdate = compareVersion(forceUpdateVersion.split(",")[0], appVersion) && compareVersion(appVersion, forceUpdateVersion.split(",")[1]);
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

        ImageText imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.LOADING_IMAGE.getType());
        List<ImageText> imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgText);
        if (imageTexts != null && imageTexts.size() > 0) {
            ImageText imageText = imageTexts.get(0);
            LoadingImageDTO loadingImageDTO = new LoadingImageDTO(imageText);
            data.put("ads", loadingImageDTO);
        }

        response.setData(data);
        return response;

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
    @VersionRange
    @WithoutToken
    public JsonResponseEntity getNavigationBar(@RequestHeader(value = "main-area", required = true) String mainArea,
                                               @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        ImageText imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.NAVIGATION_BAR.getType());
        List<ImageText> imageTexts = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgText);
        if (imageTexts != null && imageTexts.size() > 0) {
            List<String> navigationBars = new ArrayList<>();
            for (ImageText imageText : imageTexts) {
                navigationBars.add(imageText.getImgUrl());
            }
            result.setData(navigationBars);
            result.setData(navigationBars);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置信息！");
        }
        return result;
    }

    @RequestMapping(value = "/aboutApp", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity aboutApp(@RequestHeader(value = "main-area", required = true) String mainArea,
                                       @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        try {
            AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, specArea, "app.common.aboutApp");
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

    /**
     * 版本比较
     *
     * @param version_a 版本A
     * @param version_b 版本B
     * @return false-版本B小于等于版本A true-版本B大于版本A
     */
    public boolean compareVersion(String version_a, String version_b) {
        if (com.qiniu.util.StringUtils.isNullOrEmpty(version_a) || com.qiniu.util.StringUtils.isNullOrEmpty(version_b)) {
            return false;
        } else {
            String[] aVsArr = version_a.split("\\.");// . 需要转换
            String[] bVsArr = version_b.split("\\.");// . 需要转换
            int subVsLength = aVsArr.length < bVsArr.length ? aVsArr.length : bVsArr.length;// 子版本号长度
            for (int i = 0; i < subVsLength; i++) {
                if (Integer.parseInt(bVsArr[i]) == Integer.parseInt(aVsArr[i])) {
                    continue;
                }
                if (Integer.parseInt(bVsArr[i]) < Integer.parseInt(aVsArr[i])) {
                    return false;
                }
                if (Integer.parseInt(bVsArr[i]) > Integer.parseInt(aVsArr[i])) {
                    return true;
                }
            }
            if (version_b.startsWith(version_a) && version_b.length() > version_a.length()) {
                return true;
            }
        }
        return false;
    }
}

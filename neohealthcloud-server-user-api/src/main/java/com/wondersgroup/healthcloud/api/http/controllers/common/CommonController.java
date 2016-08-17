package com.wondersgroup.healthcloud.api.http.controllers.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.UploaderUtil;
/*import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.services.config.AppConfigService;*/
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

    /*@Autowired
    private AppConfigService appConfigService;*/
    /**
     * APP获取启动数据
     */
    @RequestMapping(value = "/appConfig", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Map<String, Object>> appConfig(@RequestHeader(value = "platform", required = false) String platform,
                                                             @RequestHeader(value = "screen-width", required = false) String width,
                                                             @RequestHeader(value = "screen-height", required = false) String height,
                                                             @RequestHeader(value = "app-version", required = false) String appVersion) {
        JsonResponseEntity<Map<String, Object>> response = new JsonResponseEntity<>();
        Map<String, Object> data = new HashMap<>();

        // todo zzx
        Map<String, Object> common = new HashMap<>();
        common.put("publicKey", HttpWdUtils.publicKey);
        common.put("consumerHotline", "123456");
        common.put("helpCenter", "http://www.wondersgroup.com/");
        data.put("common", common);

        Map ads = new HashMap();
        ads.put("imgUrl", "http://www.wondersgroup.com/");
        ads.put("hoplink", "http://www.wondersgroup.com/");
        ads.put("duration", 2500);
        ads.put("isSkip", true);
        ads.put("isShow", true);
        data.put("ads", ads);

        //AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord("app.common.appUpdate");
        Map appUpdate = new HashMap();
        appUpdate.put("hasUpdate", true);
        appUpdate.put("forceUpdate", false);
        appUpdate.put("appVersion", "3.0.1");
        appUpdate.put("updateMsg", "1.更新信息1,2.更新信息2,3.bug修复");
        appUpdate.put("iosUrl", "http://www.wondersgroup.com/");
        appUpdate.put("androidUrl", "http://www.wondersgroup.com/");
        data.put("appUpdate", appUpdate);

        response.setData(data);
        return response;

    }

    @RequestMapping(value = "/getQiniuToken", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> qiniuConfig() {
        JsonResponseEntity<Map<String, Object>> response = new JsonResponseEntity<Map<String, Object>>();
        // todo zzx
        Map<String, Object> map = Maps.newHashMap();
        map.put("token", UploaderUtil.getUpToken());
        map.put("expires", UploaderUtil.expires);
        map.put("domain", UploaderUtil.domain);
        response.setData(map);
        return response;
    }

    @RequestMapping(value = "/appNavigationBar", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity getNavigationBar() {
        JsonResponseEntity result = new JsonResponseEntity();
        // todo zzx
        List<String> navigationBars = new ArrayList<>();
        navigationBars.add("http://img.wdjky.com/b83024f11452254894886.png?imageView2");
        navigationBars.add("http://img.wdjky.com/6d3df5171452408025730.png?imageView2");
        navigationBars.add("http://img.wdjky.com/c5a1d7091452408035998.png?imageView2");
        navigationBars.add("http://img.wdjky.com/0f3bccf21452408043475.png?imageView2");
        navigationBars.add("http://img.wdjky.com/e3b9a88f1452408049464.png?imageView2");
        navigationBars.add("http://img.wdjky.com/b0544d041452157387796.png?imageView2");
        navigationBars.add("http://img.wdjky.com/ab1ac8b71452157428739.png?imageView2");
        navigationBars.add("http://img.wdjky.com/49e26f5a1452157439591.png?imageView2");
        navigationBars.add("http://img.wdjky.com/80f784041452157447526.png?imageView2");
        result.setData(navigationBars);

        return result;
    }

    @RequestMapping(value = "/aboutApp", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity aboutApp() {
        JsonResponseEntity result = new JsonResponseEntity();
        // todo zzx
        Map map = new HashMap();
        map.put("appDesc", "健康云");
        map.put("newVersion", "1.0");
        map.put("appExplanation", "万达信息股份有限公司");
        result.setData(map);
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

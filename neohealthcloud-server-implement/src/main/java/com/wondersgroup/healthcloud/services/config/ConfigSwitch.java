package com.wondersgroup.healthcloud.services.config;

import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 获取配置开关
 * Created by ys on 2016-12-14.
 */
@Service("configSwitch")
public class ConfigSwitch {

    @Autowired
    private AppConfigService appConfigService;

    /**
     * 是否审核话题
     */
    public Boolean isVerifyTopic() {
        return getConfigSwitchOpen("bbs.publishTopic.verify", true);
    }

    /**
     * 是否审核回帖
     */
    public Boolean isVerifyComment() {
        return getConfigSwitchOpen("bbs.publishComment.verify", false);
    }

    /**
     * 是否屏蔽违禁词
     */
    public Boolean isDealBbsBadWords() {
        return getConfigSwitchOpen("bbs.badwords.deal", true);
    }




    /**
     * 上海配置项开关获取
     * @param key 配置key
     * @param defaultSwitch 默认开关
     */
    private Boolean getConfigSwitchOpen(String key, Boolean defaultSwitch){
        return getConfigSwitchOpen(key, defaultSwitch, "3101");
    }

    /**
     * @param key 配置key
     * @param defaultSwitch 默认开关
     * @param mainArea 地区
     */
    private Boolean getConfigSwitchOpen(String key, Boolean defaultSwitch, String mainArea){
        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, key);
        if (null == appConfig){
            return defaultSwitch;
        }
        String value = appConfig.getData();
        return "1".equals(value);
    }
}

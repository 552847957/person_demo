package com.wondersgroup.healthcloud.services.config;

import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 违禁词
 * Created by ys on 2016-12-14.
 */
@Service("configSwitch")
public class ConfigSwitch {

    @Autowired
    private AppConfigService appConfigService;

    public Boolean isVerifyTopic() {
        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord("3101", null, "bbs.publishTopic.verify");
        if (null == appConfig){
            return true;
        }
        String value = appConfig.getData();
        return "1".equals(value);
    }

    public Boolean isVerifyComment() {
        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord("3101", null, "bbs.publishComment.verify");
        if (null == appConfig){
            return false;
        }
        String value = appConfig.getData();
        return "1".equals(value);
    }

    public Boolean isDealBbsBadWords() {
        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord("3101", null, "bbs.badwords.deal");
        if (null == appConfig){
            return true;
        }
        String value = appConfig.getData();
        return "1".equals(value);
    }
}

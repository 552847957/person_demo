package com.wondersgroup.healthcloud.services.config;

import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/16.
 */
public interface AppConfigService {
    Map<String, String> findAppConfigByKeyWords(String mainArea, String specArea, String[] keyWords);

    List<AppConfig> findAllDiscreteAppConfig(String mainArea, String specArea);

    AppConfig findSingleAppConfigByKeyWord(String mainArea, String specArea, String keyWord);

    AppConfig saveAndUpdateAppConfig(AppConfig appConfig);
}

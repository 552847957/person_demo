package com.wondersgroup.healthcloud.services.config;

import java.util.List;
import java.util.Map;

import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;

/**
 * Created by zhaozhenxing on 2016/8/16.
 */
public interface AppConfigService {
    Map<String, String> findAppConfigByKeyWords(String mainArea, String specArea, List<String> keyWords, String ... source);

    List<AppConfig> findAllDiscreteAppConfig(String mainArea, String specArea, String ... source);

    AppConfig findSingleAppConfigByKeyWord(String mainArea, String specArea, String keyWord, String ... source);

    AppConfig saveAndUpdateAppConfig(AppConfig appConfig);
}
